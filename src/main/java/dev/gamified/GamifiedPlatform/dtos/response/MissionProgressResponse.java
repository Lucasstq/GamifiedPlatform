package dev.gamified.GamifiedPlatform.dtos.response;

import lombok.Builder;

@Builder
public record MissionProgressResponse(
        Long levelId,
        String levelName,
        Long totalMissions,
        Long completedMissions,
        Double progressPercentage,
        Boolean canUnlockBoss
) {
}
