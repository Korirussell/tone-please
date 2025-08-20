package com.tonediscord.tone.service;

import com.tonediscord.tone.repository.DiscordMessageRepository;
import com.tonediscord.tone.repository.UserSentimentSummaryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class VibeAlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(VibeAlertService.class);
    
    private final DiscordMessageRepository messageRepository;
    private final UserSentimentSummaryRepository summaryRepository;
    private final DiscordBotService discordBotService;
    
    @Value("${tone.analysis.vibe-shift-threshold:0.5}")
    private Double vibeShiftThreshold;
    
    // Store previous vibe state for each guild
    private final Map<String, Double> previousVibes = new HashMap<>();

    public VibeAlertService(DiscordMessageRepository messageRepository,
                           UserSentimentSummaryRepository summaryRepository,
                           DiscordBotService discordBotService) {
        this.messageRepository = messageRepository;
        this.summaryRepository = summaryRepository;
        this.discordBotService = discordBotService;
    }

    @Scheduled(fixedRate = 300000) // Check every 5 minutes
    public void checkVibeShifts() {
        if (!discordBotService.isConnected()) {
            return;
        }

        try {
            // Get all active guilds
            summaryRepository.findAll().stream()
                    .map(summary -> summary.getGuildId())
                    .distinct()
                    .forEach(this::checkGuildVibeShift);
        } catch (Exception e) {
            logger.error("Error checking vibe shifts", e);
        }
    }

    private void checkGuildVibeShift(String guildId) {
        try {
            // Get current vibe (last 30 minutes)
            LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
            Double currentVibe = messageRepository.getAverageSentimentSince(guildId, thirtyMinutesAgo);
            
            if (currentVibe == null) {
                return; // Not enough recent activity
            }

            Double previousVibe = previousVibes.get(guildId);
            
            if (previousVibe != null) {
                double vibeChange = Math.abs(currentVibe - previousVibe);
                
                if (vibeChange >= vibeShiftThreshold) {
                    sendVibeShiftAlert(guildId, previousVibe, currentVibe, vibeChange);
                }
            }
            
            // Update stored vibe
            previousVibes.put(guildId, currentVibe);
            
        } catch (Exception e) {
            logger.error("Error checking vibe shift for guild {}: {}", guildId, e.getMessage());
        }
    }

    private void sendVibeShiftAlert(String guildId, Double previousVibe, Double currentVibe, double change) {
        boolean isImproving = currentVibe > previousVibe;
        String direction = isImproving ? "improved" : "declined";
        String emoji = isImproving ? "📈 ✨" : "📉 ⚠️";
        
        String alertMessage = String.format("""
                %s **Vibe Shift Alert!** %s
                
                The server vibe has %s significantly!
                📊 **Previous:** %.2f
                📊 **Current:** %.2f
                📈 **Change:** %.2f
                
                %s
                """,
                emoji, emoji,
                direction,
                previousVibe,
                currentVibe,
                change,
                getVibeShiftAdvice(isImproving, currentVibe));
        
        // For now, we'll assume there's a general channel
        // In a real implementation, you'd configure alert channels per guild
        discordBotService.sendVibeAlert(guildId, null, alertMessage);
        
        logger.info("Sent vibe shift alert for guild {}: {} -> {} ({})", 
                   guildId, previousVibe, currentVibe, direction);
    }

    private String getVibeShiftAdvice(boolean isImproving, Double currentVibe) {
        if (isImproving) {
            if (currentVibe > 0.3) {
                return "🎉 The positivity is contagious! Keep it up!";
            } else {
                return "😊 Things are looking up! Good work everyone!";
            }
        } else {
            if (currentVibe < -0.3) {
                return "😅 Maybe it's time for some wholesome memes or good news?";
            } else {
                return "🤔 The vibe is shifting. Time to spread some positivity!";
            }
        }
    }

    public void resetVibeHistory() {
        previousVibes.clear();
        logger.info("Vibe history reset");
    }
}
