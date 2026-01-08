package dev.gamified.GamifiedPlatform.dtos.response.bosses;

import lombok.Builder;

@Builder
public record BossResponse(
        Long id,
        Long levelId,
        String levelName,
        String name,
        String title,
        String description,
        String challenge,
        Integer xpReward,
        String badgeName,
        String badgeDescription,
        String imageUrl,
        String badgeIconUrl,
        Boolean unlocksNextLevel
) {
}
