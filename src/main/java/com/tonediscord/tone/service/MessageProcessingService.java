package com.tonediscord.tone.service;

import com.tonediscord.tone.dto.LeaderboardEntry;
import com.tonediscord.tone.dto.SentimentResponse;
import com.tonediscord.tone.entity.DiscordMessage;
import com.tonediscord.tone.entity.UserSentimentSummary;
import com.tonediscord.tone.repository.DiscordMessageRepository;
import com.tonediscord.tone.repository.UserSentimentSummaryRepository;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageProcessingService.class);
    
    private final DiscordMessageRepository messageRepository;
    private final UserSentimentSummaryRepository summaryRepository;
    private final SentimentAnalysisService sentimentService;
    
    @Value("${tone.analysis.sentiment-threshold:0.7}")
    private Double sentimentThreshold;
    
    @Value("${tone.analysis.vibe-shift-threshold:0.5}")
    private Double vibeShiftThreshold;

    public MessageProcessingService(DiscordMessageRepository messageRepository,
                                  UserSentimentSummaryRepository summaryRepository,
                                  SentimentAnalysisService sentimentService) {
        this.messageRepository = messageRepository;
        this.summaryRepository = summaryRepository;
        this.sentimentService = sentimentService;
    }

    @Async
    @Transactional
    public void processMessage(DiscordMessage message) {
        try {
            // Analyze sentiment
            SentimentResponse sentiment = sentimentService.analyzeSentiment(message.getMessageContent());
            
            // Update message with sentiment data
            message.setSentimentScore(sentiment.getScore());
            message.setSentimentLabel(sentiment.getLabel());
            message.setProcessed(true);
            messageRepository.save(message);
            
            // Update user summary
            updateUserSummary(message, sentiment);
            
            logger.debug("Processed message from {}: {} -> {} ({})", 
                        message.getUsername(), 
                        message.getMessageContent().substring(0, Math.min(30, message.getMessageContent().length())),
                        sentiment.getLabel(), 
                        sentiment.getScore());
            
        } catch (Exception e) {
            logger.error("Error processing message {}: {}", message.getId(), e.getMessage());
        }
    }

    private void updateUserSummary(DiscordMessage message, SentimentResponse sentiment) {
        UserSentimentSummary summary = summaryRepository
                .findByUserIdAndGuildId(message.getUserId(), message.getGuildId())
                .orElse(new UserSentimentSummary(message.getUserId(), message.getUsername(), message.getGuildId()));
        
        summary.updateWithNewMessage(sentiment.getScore(), sentiment.getLabel());
        summaryRepository.save(summary);
    }

    @Scheduled(fixedRate = 60000) // Run every minute
    @Transactional
    public void processUnprocessedMessages() {
        List<DiscordMessage> unprocessedMessages = messageRepository.findByProcessedFalse();
        
        if (!unprocessedMessages.isEmpty()) {
            logger.info("Processing {} unprocessed messages", unprocessedMessages.size());
            
            for (DiscordMessage message : unprocessedMessages) {
                processMessage(message);
            }
        }
    }

    public void sendStatsMessage(MessageReceivedEvent event) {
        String guildId = event.getGuild().getId();
        
        try {
            List<UserSentimentSummary> summaries = summaryRepository.findByGuildId(guildId);
            Long totalMessages = summaries.stream()
                    .mapToLong(s -> s.getTotalMessages().longValue())
                    .sum();
            
            Double avgSentiment = summaryRepository.getGuildAverageSentiment(guildId);
            if (avgSentiment == null) avgSentiment = 0.0;
            
            String vibeEmoji = sentimentService.getSentimentEmoji(
                    avgSentiment > 0 ? "POSITIVE" : avgSentiment < 0 ? "NEGATIVE" : "NEUTRAL", 
                    avgSentiment);
            
            String statsMessage = String.format("""
                    %s **Server Vibe Stats** %s
                    
                    📊 **Total Messages Analyzed:** %,d
                    👥 **Active Users:** %d
                    🌡️ **Average Sentiment:** %.2f (%s)
                    
                    Use `!tone leaderboard` to see who's the meanest! 😈
                    """, 
                    vibeEmoji, vibeEmoji,
                    totalMessages,
                    summaries.size(),
                    avgSentiment,
                    getSentimentDescription(avgSentiment));
            
            event.getChannel().sendMessage(statsMessage).queue();
            
        } catch (Exception e) {
            logger.error("Error generating stats message", e);
            event.getChannel().sendMessage("❌ Sorry, couldn't generate stats right now!").queue();
        }
    }

    public void sendLeaderboardMessage(MessageReceivedEvent event) {
        String guildId = event.getGuild().getId();
        
        try {
            List<UserSentimentSummary> meanest = summaryRepository.findMeanestUsers(guildId)
                    .stream()
                    .limit(5)
                    .collect(Collectors.toList());
            
            List<UserSentimentSummary> nicest = summaryRepository.findMostPositiveUsers(guildId)
                    .stream()
                    .limit(5)
                    .collect(Collectors.toList());
            
            StringBuilder leaderboard = new StringBuilder("🏆 **Vibe Leaderboard** 🏆\n\n");
            
            leaderboard.append("😈 **Meanest Members:**\n");
            for (int i = 0; i < meanest.size(); i++) {
                UserSentimentSummary user = meanest.get(i);
                leaderboard.append(String.format("%d. **%s** - %.2f sentiment (%d messages)\n",
                        i + 1, user.getUsername(), user.getAverageSentiment(), user.getTotalMessages()));
            }
            
            leaderboard.append("\n😇 **Nicest Members:**\n");
            for (int i = 0; i < nicest.size(); i++) {
                UserSentimentSummary user = nicest.get(i);
                leaderboard.append(String.format("%d. **%s** - %.2f sentiment (%d messages)\n",
                        i + 1, user.getUsername(), user.getAverageSentiment(), user.getTotalMessages()));
            }
            
            event.getChannel().sendMessage(leaderboard.toString()).queue();
            
        } catch (Exception e) {
            logger.error("Error generating leaderboard", e);
            event.getChannel().sendMessage("❌ Sorry, couldn't generate leaderboard right now!").queue();
        }
    }

    public void sendVibeCheckMessage(MessageReceivedEvent event) {
        String guildId = event.getGuild().getId();
        
        try {
            // Get recent sentiment (last hour)
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            Double recentSentiment = messageRepository.getAverageSentimentSince(guildId, oneHourAgo);
            
            if (recentSentiment == null) {
                event.getChannel().sendMessage("🤔 Not enough recent activity to check the vibe!").queue();
                return;
            }
            
            String emoji = sentimentService.getSentimentEmoji(
                    recentSentiment > 0 ? "POSITIVE" : recentSentiment < 0 ? "NEGATIVE" : "NEUTRAL",
                    recentSentiment);
            
            String vibeMessage = String.format("""
                    %s **Current Server Vibe Check** %s
                    
                    🌡️ **Recent Sentiment:** %.2f
                    📊 **Vibe Status:** %s
                    ⏰ **Based on:** Last hour of messages
                    
                    %s
                    """,
                    emoji, emoji,
                    recentSentiment,
                    getSentimentDescription(recentSentiment),
                    getVibeAdvice(recentSentiment));
            
            event.getChannel().sendMessage(vibeMessage).queue();
            
        } catch (Exception e) {
            logger.error("Error generating vibe check", e);
            event.getChannel().sendMessage("❌ Sorry, couldn't check the vibe right now!").queue();
        }
    }

    private String getSentimentDescription(Double score) {
        if (score == null) return "Unknown vibes";
        if (score > 0.5) return "Amazing vibes! Everyone's happy! ✨";
        if (score > 0.1) return "Good vibes 👍";
        if (score > -0.1) return "Neutral vibes 😐";
        if (score > -0.5) return "Kinda negative vibes 👎";
        return "Very toxic vibes 💀";
    }

    private String getVibeAdvice(Double score) {
        if (score == null) return "";
        if (score > 0.3) return "Keep spreading the positivity! 🌟";
        if (score > 0) return "Things are looking good! 😊";
        if (score > -0.3) return "Maybe it's time for some good news? 📰";
        return "Someone should tell a joke or share a meme! 😅";
    }
}
