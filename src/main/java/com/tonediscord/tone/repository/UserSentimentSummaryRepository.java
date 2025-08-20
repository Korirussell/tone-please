package com.tonediscord.tone.repository;

import com.tonediscord.tone.entity.UserSentimentSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSentimentSummaryRepository extends JpaRepository<UserSentimentSummary, Long> {
    
    Optional<UserSentimentSummary> findByUserIdAndGuildId(String userId, String guildId);
    
    List<UserSentimentSummary> findByGuildId(String guildId);
    
    @Query("SELECT uss FROM UserSentimentSummary uss WHERE uss.guildId = :guildId ORDER BY uss.averageSentiment ASC")
    List<UserSentimentSummary> findMeanestUsers(@Param("guildId") String guildId);
    
    @Query("SELECT uss FROM UserSentimentSummary uss WHERE uss.guildId = :guildId ORDER BY uss.averageSentiment DESC")
    List<UserSentimentSummary> findMostPositiveUsers(@Param("guildId") String guildId);
    
    @Query("SELECT uss FROM UserSentimentSummary uss WHERE uss.guildId = :guildId ORDER BY uss.totalMessages DESC")
    List<UserSentimentSummary> findMostActiveUsers(@Param("guildId") String guildId);
    
    @Query("SELECT AVG(uss.averageSentiment) FROM UserSentimentSummary uss WHERE uss.guildId = :guildId")
    Double getGuildAverageSentiment(@Param("guildId") String guildId);
}
