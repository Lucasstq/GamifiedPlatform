package dev.gamified.GamifiedPlatform.dtos.response.admin;

import lombok.Builder;

@Builder
public record AdminDashboardResponse(
        Long totalUsers,
        Long activeUsers,
        Long totalMissions,
        Long totalBosses,
        Long totalLevels,
        Long totalBadges,
        Double averageCompletionRate,
        MissionDifficultyStats missionDifficultyStats,
        BossDefeatedStats bossDefeatedStats
) {
    @Builder
    public record MissionDifficultyStats(
            Long missionId,
            String missionTitle,
            Long totalAttempts,
            Long failedAttempts,
            Double failureRate
    ) {}

    @Builder
    public record BossDefeatedStats(
            Long bossId,
            String bossName,
            Long totalAttempts,
            Long defeated,
            Double defeatRate
    ) {}
}


