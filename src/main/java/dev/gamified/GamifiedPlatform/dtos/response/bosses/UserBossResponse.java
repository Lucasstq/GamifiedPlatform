package dev.gamified.GamifiedPlatform.dtos.response.bosses;

import dev.gamified.GamifiedPlatform.enums.BossFightStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserBossResponse(
        Long id,
        Long bossId,
        String bossName,
        String bossTitle,
        String bossDescription,
        String bossChallenge,
        Integer xpReward,
        String badgeName,
        String badgeDescription,
        String imageUrl,
        String badgeIconUrl,
        BossFightStatus status,
        String submissionUrl,
        String submissionNotes,
        String feedback,
        String evaluatedByName,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        LocalDateTime evaluatedAt,
        LocalDateTime completedAt,
        LocalDateTime unlockedAt
) {
}

