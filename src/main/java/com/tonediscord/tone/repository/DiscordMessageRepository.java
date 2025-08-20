package com.tonediscord.tone.repository;

import com.tonediscord.tone.entity.DiscordMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscordMessageRepository extends JpaRepository<DiscordMessage, Long> {
    
    List<DiscordMessage> findByProcessedFalse();
    
    boolean existsByDiscordMessageId(String discordMessageId);
    
    List<DiscordMessage> findByGuildIdAndTimestampBetween(String guildId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT dm FROM DiscordMessage dm WHERE dm.guildId = :guildId AND dm.processed = true ORDER BY dm.timestamp DESC")
    List<DiscordMessage> findRecentProcessedMessages(@Param("guildId") String guildId);
    
    @Query("SELECT AVG(dm.sentimentScore) FROM DiscordMessage dm WHERE dm.guildId = :guildId AND dm.timestamp >= :since AND dm.processed = true")
    Double getAverageSentimentSince(@Param("guildId") String guildId, @Param("since") LocalDateTime since);
    
    @Query("SELECT dm FROM DiscordMessage dm WHERE dm.guildId = :guildId AND dm.sentimentScore IS NOT NULL ORDER BY dm.sentimentScore ASC")
    List<DiscordMessage> findMostNegativeMessages(@Param("guildId") String guildId);
    
    @Query("SELECT dm FROM DiscordMessage dm WHERE dm.guildId = :guildId AND dm.sentimentScore IS NOT NULL ORDER BY dm.sentimentScore DESC")
    List<DiscordMessage> findMostPositiveMessages(@Param("guildId") String guildId);
    
    @Query("SELECT COUNT(dm) FROM DiscordMessage dm WHERE dm.userId = :userId AND dm.guildId = :guildId AND dm.processed = true")
    Long countByUserIdAndGuildId(@Param("userId") String userId, @Param("guildId") String guildId);
}
