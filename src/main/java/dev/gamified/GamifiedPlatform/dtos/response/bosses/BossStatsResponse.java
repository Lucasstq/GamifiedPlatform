package dev.gamified.GamifiedPlatform.dtos.response.bosses;

import lombok.Builder;

import java.util.List;

@Builder
public record BossStatsResponse(
        List<BossStat> undefeatedBosses,
        List<BossStat> mostDefeatedBosses,
        List<BossStat> hardestBosses,
        Long totalBossesCount,
        Long totalBossAttempts,
        Double averageDefeatRate
) {
    @Builder
    public record BossStat(
            Long bossId,
            String bossName,
            String levelName,
            Long totalAttempts,
            Long totalDefeats,
            Long totalFailures,
            Double defeatRate,
            Double failureRate
    ) {}
}
