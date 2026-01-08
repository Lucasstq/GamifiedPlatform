package dev.gamified.GamifiedPlatform.dtos.response.levels;

import lombok.Builder;

import java.util.List;

@Builder
public record LevelCompletionStatsResponse(
        Long levelId,
        String levelName,
        Integer orderLevel,
        Long totalUsers,
        Long usersCompleted,
        Double completionRate,
        Long totalMissions,
        Double averageProgress,
        List<MissionStatsInLevel> missionStats
) {
    @Builder
    public record MissionStatsInLevel(
            Long missionId,
            String missionTitle,
            Long totalAttempts,
            Long completed,
            Long failed,
            Double completionRate
    ) {}
}
