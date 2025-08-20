package com.tonediscord.tone.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sentiment_summary")
public class UserSentimentSummary {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "username", nullable = false)
    private String username;
    
    @Column(name = "guild_id", nullable = false)
    private String guildId;
    
    @Column(name = "total_messages", nullable = false)
    private Integer totalMessages = 0;
    
    @Column(name = "positive_messages", nullable = false)
    private Integer positiveMessages = 0;
    
    @Column(name = "negative_messages", nullable = false)
    private Integer negativeMessages = 0;
    
    @Column(name = "neutral_messages", nullable = false)
    private Integer neutralMessages = 0;
    
    @Column(name = "average_sentiment", nullable = false)
    private Double averageSentiment = 0.0;
    
    @Column(name = "most_positive_score")
    private Double mostPositiveScore;
    
    @Column(name = "most_negative_score")
    private Double mostNegativeScore;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructors
    public UserSentimentSummary() {}

    public UserSentimentSummary(String userId, String username, String guildId) {
        this.userId = userId;
        this.username = username;
        this.guildId = guildId;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getGuildId() { return guildId; }
    public void setGuildId(String guildId) { this.guildId = guildId; }

    public Integer getTotalMessages() { return totalMessages; }
    public void setTotalMessages(Integer totalMessages) { this.totalMessages = totalMessages; }

    public Integer getPositiveMessages() { return positiveMessages; }
    public void setPositiveMessages(Integer positiveMessages) { this.positiveMessages = positiveMessages; }

    public Integer getNegativeMessages() { return negativeMessages; }
    public void setNegativeMessages(Integer negativeMessages) { this.negativeMessages = negativeMessages; }

    public Integer getNeutralMessages() { return neutralMessages; }
    public void setNeutralMessages(Integer neutralMessages) { this.neutralMessages = neutralMessages; }

    public Double getAverageSentiment() { return averageSentiment; }
    public void setAverageSentiment(Double averageSentiment) { this.averageSentiment = averageSentiment; }

    public Double getMostPositiveScore() { return mostPositiveScore; }
    public void setMostPositiveScore(Double mostPositiveScore) { this.mostPositiveScore = mostPositiveScore; }

    public Double getMostNegativeScore() { return mostNegativeScore; }
    public void setMostNegativeScore(Double mostNegativeScore) { this.mostNegativeScore = mostNegativeScore; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    // Helper methods
    public void updateWithNewMessage(Double sentimentScore, String sentimentLabel) {
        this.totalMessages++;
        
        if ("POSITIVE".equalsIgnoreCase(sentimentLabel)) {
            this.positiveMessages++;
        } else if ("NEGATIVE".equalsIgnoreCase(sentimentLabel)) {
            this.negativeMessages++;
        } else {
            this.neutralMessages++;
        }
        
        // Update average sentiment
        this.averageSentiment = ((this.averageSentiment * (this.totalMessages - 1)) + sentimentScore) / this.totalMessages;
        
        // Update extremes
        if (this.mostPositiveScore == null || sentimentScore > this.mostPositiveScore) {
            this.mostPositiveScore = sentimentScore;
        }
        if (this.mostNegativeScore == null || sentimentScore < this.mostNegativeScore) {
            this.mostNegativeScore = sentimentScore;
        }
        
        this.lastUpdated = LocalDateTime.now();
    }
}
