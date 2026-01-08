package dev.gamified.GamifiedPlatform.dtos.response.bosses;

import lombok.Builder;

@Builder
public record BossProgressResponse(
        Long levelId,
        String levelName,
        Long totalMissions,
        Long completedMissions,
        Double progressPercentage,
        Boolean bossUnlocked,
        UserBossResponse bossStatus
) {
}

