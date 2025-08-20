package com.tonediscord.tone.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tonediscord.tone.dto.SentimentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class SentimentAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(SentimentAnalysisService.class);
    
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    
    @Value("${ai.service.huggingface.api-url}")
    private String huggingFaceApiUrl;
    
    @Value("${ai.service.huggingface.api-key}")
    private String huggingFaceApiKey;

    public SentimentAnalysisService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
        this.objectMapper = objectMapper;
    }

    public SentimentResponse analyzeSentiment(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new SentimentResponse("NEUTRAL", 0.0);
        }

        try {
            // Clean the text for analysis
            String cleanText = cleanText(text);
            logger.debug("Analyzing sentiment for cleaned text: {}", cleanText);
            
            // Prepare the request payload
            Map<String, Object> requestBody = Map.of("inputs", cleanText);
            logger.debug("Sending request to Hugging Face API: {}", huggingFaceApiUrl);
            
            // Call Hugging Face API with retry logic
            List<List<Map<String, Object>>> response = webClient.post()
                    .uri(huggingFaceApiUrl)
                    .header("Authorization", "Bearer " + huggingFaceApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(new org.springframework.core.ParameterizedTypeReference<List<List<Map<String, Object>>>>() {})
                    .timeout(Duration.ofSeconds(30))
                    .retry(2) // Retry up to 2 times for transient failures
                    .block();

            return parseSentimentResponse(response);
            
        } catch (Exception e) {
            logger.error("Error analyzing sentiment for text: {}", text, e);
            return new SentimentResponse("NEUTRAL", 0.0);
        }
    }

    private String cleanText(String text) {
        // Remove Discord mentions, emojis, and excessive whitespace
        return text.replaceAll("<@[!&]?\\d+>", "") // Remove mentions
                   .replaceAll("<#\\d+>", "") // Remove channel mentions
                   .replaceAll("<:\\w+:\\d+>", "") // Remove custom emojis
                   .replaceAll("\\s+", " ") // Normalize whitespace
                   .trim();
    }

    private SentimentResponse parseSentimentResponse(List<List<Map<String, Object>>> response) {
        try {
            if (response != null && !response.isEmpty() && !response.get(0).isEmpty()) {
                List<Map<String, Object>> sentiments = response.get(0);
                
                // Find the sentiment with the highest score
                Map<String, Object> bestSentiment = sentiments.stream()
                        .max((s1, s2) -> Double.compare(
                                ((Number) s1.get("score")).doubleValue(),
                                ((Number) s2.get("score")).doubleValue()))
                        .orElse(sentiments.get(0));
                
                String label = (String) bestSentiment.get("label");
                Double score = ((Number) bestSentiment.get("score")).doubleValue();
                
                // Convert to our sentiment scale (-1 to 1)
                Double normalizedScore = normalizeSentimentScore(label, score);
                String normalizedLabel = normalizeLabel(label);
                
                return new SentimentResponse(normalizedLabel, normalizedScore);
            }
        } catch (Exception e) {
            logger.error("Error parsing sentiment response", e);
        }
        
        return new SentimentResponse("NEUTRAL", 0.0);
    }

    private Double normalizeSentimentScore(String label, Double score) {
        // Convert Hugging Face sentiment to our -1 to 1 scale
        switch (label.toLowerCase()) {
            case "label_0": // NEGATIVE
            case "negative":
                return -score; // Make negative
            case "label_1": // NEUTRAL
            case "neutral":
                return 0.0; // Keep neutral
            case "label_2": // POSITIVE
            case "positive":
                return score; // Keep positive
            default:
                return 0.0;
        }
    }

    private String normalizeLabel(String label) {
        switch (label.toLowerCase()) {
            case "label_0":
            case "negative":
                return "NEGATIVE";
            case "label_1":
            case "neutral":
                return "NEUTRAL";
            case "label_2":
            case "positive":
                return "POSITIVE";
            default:
                return "NEUTRAL";
        }
    }

    public boolean isSignificantSentiment(Double score, Double threshold) {
        return score != null && Math.abs(score) >= threshold;
    }

    public String getSentimentEmoji(String label, Double score) {
        if (score == null) return "😐";
        
        switch (label) {
            case "POSITIVE":
                return score > 0.7 ? "😍" : "😊";
            case "NEGATIVE":
                return score < -0.7 ? "😡" : "😔";
            default:
                return "😐";
        }
    }
}
