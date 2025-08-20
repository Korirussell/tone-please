package com.tonediscord.tone.dto;

public class LeaderboardEntry {
    
    private String username;
    private String displayName;
    private Integer totalMessages;
    private Double averageSentiment;
    private Integer positiveMessages;
    private Integer negativeMessages;
    private Integer neutralMessages;
    private String sentimentCategory;

    public LeaderboardEntry() {}

    public LeaderboardEntry(String username, String displayName, Integer totalMessages, 
                           Double averageSentiment, Integer positiveMessages, 
                           Integer negativeMessages, Integer neutralMessages) {
        this.username = username;
        this.displayName = displayName;
        this.totalMessages = totalMessages;
        this.averageSentiment = averageSentiment;
        this.positiveMessages = positiveMessages;
        this.negativeMessages = negativeMessages;
        this.neutralMessages = neutralMessages;
        this.sentimentCategory = categorizesentiment(averageSentiment);
    }

    private String categorizesentiment(Double sentiment) {
        if (sentiment == null) return "Unknown";
        if (sentiment > 0.1) return "Positive Vibes";
        if (sentiment < -0.1) return "Negative Nancy";
        return "Neutral Zone";
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Integer getTotalMessages() { return totalMessages; }
    public void setTotalMessages(Integer totalMessages) { this.totalMessages = totalMessages; }

    public Double getAverageSentiment() { return averageSentiment; }
    public void setAverageSentiment(Double averageSentiment) { 
        this.averageSentiment = averageSentiment;
        this.sentimentCategory = categorizesentiment(averageSentiment);
    }

    public Integer getPositiveMessages() { return positiveMessages; }
    public void setPositiveMessages(Integer positiveMessages) { this.positiveMessages = positiveMessages; }

    public Integer getNegativeMessages() { return negativeMessages; }
    public void setNegativeMessages(Integer negativeMessages) { this.negativeMessages = negativeMessages; }

    public Integer getNeutralMessages() { return neutralMessages; }
    public void setNeutralMessages(Integer neutralMessages) { this.neutralMessages = neutralMessages; }

    public String getSentimentCategory() { return sentimentCategory; }
    public void setSentimentCategory(String sentimentCategory) { this.sentimentCategory = sentimentCategory; }
}
