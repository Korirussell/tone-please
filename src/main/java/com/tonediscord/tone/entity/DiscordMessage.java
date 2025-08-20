package com.tonediscord.tone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "discord_messages")
public class DiscordMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "discord_message_id", unique = true, nullable = false)
    private String discordMessageId;
    
    @Column(name = "channel_id", nullable = false)
    private String channelId;
    
    @Column(name = "channel_name")
    private String channelName;
    
    @Column(name = "guild_id", nullable = false)
    private String guildId;
    
    @Column(name = "guild_name")
    private String guildName;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "display_name")
    private String displayName;
    
    @Column(name = "message_content", columnDefinition = "TEXT")
    private String messageContent;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "sentiment_score")
    private Double sentimentScore;
    
    @Column(name = "sentiment_label")
    private String sentimentLabel;
    
    @Column(name = "processed", nullable = false)
    private Boolean processed = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public DiscordMessage() {}

    public DiscordMessage(String discordMessageId, String channelId, String channelName, 
                         String guildId, String guildName, String userId, String username, 
                         String displayName, String messageContent, LocalDateTime timestamp) {
        this.discordMessageId = discordMessageId;
        this.channelId = channelId;
        this.channelName = channelName;
        this.guildId = guildId;
        this.guildName = guildName;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDiscordMessageId() { return discordMessageId; }
    public void setDiscordMessageId(String discordMessageId) { this.discordMessageId = discordMessageId; }

    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }

    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }

    public String getGuildId() { return guildId; }
    public void setGuildId(String guildId) { this.guildId = guildId; }

    public String getGuildName() { return guildName; }
    public void setGuildName(String guildName) { this.guildName = guildName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }

    public String getSentimentLabel() { return sentimentLabel; }
    public void setSentimentLabel(String sentimentLabel) { this.sentimentLabel = sentimentLabel; }

    public Boolean getProcessed() { return processed; }
    public void setProcessed(Boolean processed) { this.processed = processed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
