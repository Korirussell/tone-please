package com.tonediscord.tone.service;

import com.tonediscord.tone.dto.SentimentResponse;
import com.tonediscord.tone.entity.DiscordMessage;
import com.tonediscord.tone.repository.DiscordMessageRepository;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.CompletableFuture;

@Service
public class DiscordBotService extends ListenerAdapter {
    
    private static final Logger logger = LoggerFactory.getLogger(DiscordBotService.class);
    
    private final DiscordMessageRepository messageRepository;
    private final SentimentAnalysisService sentimentService;
    private final MessageProcessingService messageProcessingService;
    
    @Value("${discord.bot.token}")
    private String botToken;
    
    private JDA jda;

    public DiscordBotService(DiscordMessageRepository messageRepository, 
                           SentimentAnalysisService sentimentService,
                           MessageProcessingService messageProcessingService) {
        this.messageRepository = messageRepository;
        this.sentimentService = sentimentService;
        this.messageProcessingService = messageProcessingService;
    }

    @PostConstruct
    public void initializeBot() {
        if ("YOUR_BOT_TOKEN_HERE".equals(botToken)) {
            logger.warn("Discord bot token not configured. Bot will not start.");
            return;
        }

        try {
            this.jda = JDABuilder.createDefault(botToken)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .addEventListeners(this)
                    .setActivity(Activity.watching("the vibes 📊"))
                    .build();
            
            logger.info("Discord bot is starting up...");
        } catch (Exception e) {
            logger.error("Failed to initialize Discord bot", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
            logger.info("Discord bot shut down");
        }
    }

    @Override
    public void onReady(ReadyEvent event) {
        logger.info("Discord bot '{}' is ready! Connected to {} guilds", 
                   event.getJDA().getSelfUser().getName(),
                   event.getJDA().getGuilds().size());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore bot messages
        if (event.getAuthor().isBot()) {
            return;
        }

        Message message = event.getMessage();
        String content = message.getContentRaw();
        
        // Skip empty messages or commands
        if (content.trim().isEmpty() || content.startsWith("!")) {
            return;
        }

        // Handle tone commands
        if (content.toLowerCase().startsWith("!tone")) {
            handleToneCommand(event, content);
            return;
        }

        // Store and process regular messages
        CompletableFuture.runAsync(() -> {
            try {
                storeMessage(event);
            } catch (Exception e) {
                logger.error("Error processing message from {}: {}", 
                           event.getAuthor().getName(), e.getMessage());
            }
        });
    }

    private void storeMessage(MessageReceivedEvent event) {
        Message message = event.getMessage();
        
        // Check if message already exists
        if (messageRepository.existsByDiscordMessageId(message.getId())) {
            return;
        }

        // Create and save message entity
        DiscordMessage discordMessage = new DiscordMessage(
                message.getId(),
                message.getChannel().getId(),
                message.getChannel().getName(),
                event.getGuild().getId(),
                event.getGuild().getName(),
                event.getAuthor().getId(),
                event.getAuthor().getName(),
                event.getMember() != null ? event.getMember().getEffectiveName() : event.getAuthor().getName(),
                message.getContentRaw(),
                LocalDateTime.ofInstant(message.getTimeCreated().toInstant(), ZoneOffset.UTC)
        );

        discordMessage = messageRepository.save(discordMessage);
        logger.debug("Stored message from {}: {}", event.getAuthor().getName(), 
                    message.getContentRaw().substring(0, Math.min(50, message.getContentRaw().length())));

        // Process sentiment analysis
        messageProcessingService.processMessage(discordMessage);
    }

    private void handleToneCommand(MessageReceivedEvent event, String content) {
        String[] parts = content.split("\\s+", 2);
        
        if (parts.length < 2) {
            sendHelpMessage(event);
            return;
        }

        String command = parts[1].toLowerCase();
        
        switch (command) {
            case "help":
                sendHelpMessage(event);
                break;
            case "stats":
                messageProcessingService.sendStatsMessage(event);
                break;
            case "leaderboard":
            case "meanest":
                messageProcessingService.sendLeaderboardMessage(event);
                break;
            case "vibe":
            case "mood":
                messageProcessingService.sendVibeCheckMessage(event);
                break;
            case "analyze":
                if (parts.length > 2) {
                    analyzeText(event, parts[2]);
                } else {
                    event.getChannel().sendMessage("Please provide text to analyze! Usage: `!tone analyze your text here`").queue();
                }
                break;
            default:
                sendHelpMessage(event);
        }
    }

    private void analyzeText(MessageReceivedEvent event, String text) {
        SentimentResponse sentiment = sentimentService.analyzeSentiment(text);
        String emoji = sentimentService.getSentimentEmoji(sentiment.getLabel(), sentiment.getScore());
        
        String response = String.format("%s **Sentiment Analysis** %s\n" +
                                      "**Text:** %s\n" +
                                      "**Sentiment:** %s (%.2f)\n" +
                                      "**Vibe:** %s",
                                      emoji, emoji,
                                      text.length() > 100 ? text.substring(0, 100) + "..." : text,
                                      sentiment.getLabel().toLowerCase(),
                                      sentiment.getScore(),
                                      getSentimentDescription(sentiment.getScore()));
        
        event.getChannel().sendMessage(response).queue();
    }

    private String getSentimentDescription(Double score) {
        if (score == null) return "Unknown vibes";
        if (score > 0.5) return "Very positive vibes! ✨";
        if (score > 0.1) return "Good vibes 👍";
        if (score > -0.1) return "Neutral vibes 😐";
        if (score > -0.5) return "Kinda negative vibes 👎";
        return "Very negative vibes 💀";
    }

    private void sendHelpMessage(MessageReceivedEvent event) {
        String helpText = """
                🤖 **Tone Bot Commands**
                
                `!tone help` - Show this help message
                `!tone stats` - Show server sentiment statistics
                `!tone leaderboard` - Show the meanest/nicest members
                `!tone vibe` - Check the current server vibe
                `!tone analyze <text>` - Analyze sentiment of specific text
                
                I automatically analyze all messages for sentiment and track the vibes! 📊
                """;
        
        event.getChannel().sendMessage(helpText).queue();
    }

    public void sendVibeAlert(String guildId, String channelId, String alertMessage) {
        if (jda == null) return;
        
        try {
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.sendMessage(alertMessage).queue();
            }
        } catch (Exception e) {
            logger.error("Failed to send vibe alert to channel {}: {}", channelId, e.getMessage());
        }
    }

    public boolean isConnected() {
        return jda != null && jda.getStatus() == JDA.Status.CONNECTED;
    }
}
