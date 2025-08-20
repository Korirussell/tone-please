package com.tonediscord.tone.controller;

import com.tonediscord.tone.dto.LeaderboardEntry;
import com.tonediscord.tone.dto.SentimentResponse;
import com.tonediscord.tone.entity.DiscordMessage;
import com.tonediscord.tone.entity.UserSentimentSummary;
import com.tonediscord.tone.repository.DiscordMessageRepository;
import com.tonediscord.tone.repository.UserSentimentSummaryRepository;
import com.tonediscord.tone.service.SentimentAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tone")
@CrossOrigin(origins = "*") // For development - configure properly for production
public class ToneController {
    
    private final DiscordMessageRepository messageRepository;
    private final UserSentimentSummaryRepository summaryRepository;
    private final SentimentAnalysisService sentimentService;

    public ToneController(DiscordMessageRepository messageRepository,
                         UserSentimentSummaryRepository summaryRepository,
                         SentimentAnalysisService sentimentService) {
        this.messageRepository = messageRepository;
        this.summaryRepository = summaryRepository;
        this.sentimentService = sentimentService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<SentimentResponse> analyzeSentiment(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        SentimentResponse sentiment = sentimentService.analyzeSentiment(text);
        return ResponseEntity.ok(sentiment);
    }

    @GetMapping("/debug/config")
    public ResponseEntity<Map<String, String>> debugConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("status", "API is running");
        config.put("huggingFaceConfigured", System.getenv("HUGGINGFACE_API_KEY") != null ? "Yes" : "No");
        config.put("discordConfigured", System.getenv("DISCORD_BOT_TOKEN") != null ? "Yes" : "No");
        return ResponseEntity.ok(config);
    }

    @GetMapping("/guilds/{guildId}/stats")
    public ResponseEntity<Map<String, Object>> getGuildStats(@PathVariable String guildId) {
        List<UserSentimentSummary> summaries = summaryRepository.findByGuildId(guildId);
        
        Long totalMessages = summaries.stream()
                .mapToLong(s -> s.getTotalMessages().longValue())
                .sum();
        
        Double avgSentiment = summaryRepository.getGuildAverageSentiment(guildId);
        if (avgSentiment == null) avgSentiment = 0.0;
        
        Long positiveMessages = summaries.stream()
                .mapToLong(s -> s.getPositiveMessages().longValue())
                .sum();
        
        Long negativeMessages = summaries.stream()
                .mapToLong(s -> s.getNegativeMessages().longValue())
                .sum();
        
        Long neutralMessages = summaries.stream()
                .mapToLong(s -> s.getNeutralMessages().longValue())
                .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMessages", totalMessages);
        stats.put("activeUsers", summaries.size());
        stats.put("averageSentiment", avgSentiment);
        stats.put("positiveMessages", positiveMessages);
        stats.put("negativeMessages", negativeMessages);
        stats.put("neutralMessages", neutralMessages);
        stats.put("sentimentDistribution", Map.of(
                "positive", positiveMessages,
                "negative", negativeMessages,
                "neutral", neutralMessages
        ));
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/guilds/{guildId}/leaderboard")
    public ResponseEntity<Map<String, List<LeaderboardEntry>>> getLeaderboard(
            @PathVariable String guildId,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<LeaderboardEntry> meanest = summaryRepository.findMeanestUsers(guildId)
                .stream()
                .limit(limit)
                .map(this::toLeaderboardEntry)
                .collect(Collectors.toList());
        
        List<LeaderboardEntry> nicest = summaryRepository.findMostPositiveUsers(guildId)
                .stream()
                .limit(limit)
                .map(this::toLeaderboardEntry)
                .collect(Collectors.toList());
        
        List<LeaderboardEntry> mostActive = summaryRepository.findMostActiveUsers(guildId)
                .stream()
                .limit(limit)
                .map(this::toLeaderboardEntry)
                .collect(Collectors.toList());
        
        Map<String, List<LeaderboardEntry>> leaderboard = new HashMap<>();
        leaderboard.put("meanest", meanest);
        leaderboard.put("nicest", nicest);
        leaderboard.put("mostActive", mostActive);
        
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/guilds/{guildId}/sentiment-history")
    public ResponseEntity<Map<String, Object>> getSentimentHistory(
            @PathVariable String guildId,
            @RequestParam(defaultValue = "7") int days) {
        
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<DiscordMessage> messages = messageRepository.findByGuildIdAndTimestampBetween(
                guildId, since, LocalDateTime.now());
        
        // Group by hour and calculate average sentiment
        Map<String, Double> hourlyData = messages.stream()
                .filter(m -> m.getSentimentScore() != null)
                .collect(Collectors.groupingBy(
                        m -> m.getTimestamp().toLocalDate().toString() + " " + 
                             String.format("%02d:00", m.getTimestamp().getHour()),
                        Collectors.averagingDouble(DiscordMessage::getSentimentScore)
                ));
        
        Map<String, Object> history = new HashMap<>();
        history.put("hourlyData", hourlyData);
        history.put("totalMessages", messages.size());
        history.put("daysCovered", days);
        
        return ResponseEntity.ok(history);
    }

    @GetMapping("/guilds/{guildId}/recent-vibe")
    public ResponseEntity<Map<String, Object>> getRecentVibe(
            @PathVariable String guildId,
            @RequestParam(defaultValue = "1") int hours) {
        
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        Double recentSentiment = messageRepository.getAverageSentimentSince(guildId, since);
        
        if (recentSentiment == null) {
            recentSentiment = 0.0;
        }
        
        String sentimentLabel = recentSentiment > 0.1 ? "POSITIVE" : 
                               recentSentiment < -0.1 ? "NEGATIVE" : "NEUTRAL";
        
        Map<String, Object> vibe = new HashMap<>();
        vibe.put("averageSentiment", recentSentiment);
        vibe.put("sentimentLabel", sentimentLabel);
        vibe.put("emoji", sentimentService.getSentimentEmoji(sentimentLabel, recentSentiment));
        vibe.put("description", getSentimentDescription(recentSentiment));
        vibe.put("hoursCovered", hours);
        
        return ResponseEntity.ok(vibe);
    }

    @GetMapping("/guilds/{guildId}/extreme-messages")
    public ResponseEntity<Map<String, List<DiscordMessage>>> getExtremeMessages(
            @PathVariable String guildId,
            @RequestParam(defaultValue = "5") int limit) {
        
        List<DiscordMessage> mostPositive = messageRepository.findMostPositiveMessages(guildId)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
        
        List<DiscordMessage> mostNegative = messageRepository.findMostNegativeMessages(guildId)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
        
        Map<String, List<DiscordMessage>> extremes = new HashMap<>();
        extremes.put("mostPositive", mostPositive);
        extremes.put("mostNegative", mostNegative);
        
        return ResponseEntity.ok(extremes);
    }

    private LeaderboardEntry toLeaderboardEntry(UserSentimentSummary summary) {
        return new LeaderboardEntry(
                summary.getUsername(),
                summary.getUsername(), // displayName - could be enhanced
                summary.getTotalMessages(),
                summary.getAverageSentiment(),
                summary.getPositiveMessages(),
                summary.getNegativeMessages(),
                summary.getNeutralMessages()
        );
    }

    private String getSentimentDescription(Double score) {
        if (score == null) return "Unknown vibes";
        if (score > 0.5) return "Amazing vibes! Everyone's happy!";
        if (score > 0.1) return "Good vibes";
        if (score > -0.1) return "Neutral vibes";
        if (score > -0.5) return "Kinda negative vibes";
        return "Very toxic vibes";
    }
}
