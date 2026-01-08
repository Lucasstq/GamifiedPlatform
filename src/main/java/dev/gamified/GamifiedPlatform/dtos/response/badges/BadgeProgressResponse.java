package dev.gamified.GamifiedPlatform.dtos.response;

import lombok.Builder;

@Builder
public record BadgeProgressResponse(
        Long totalBadges,
        Long unlockedBadges,
        Double progressPercentage,
        Long remainingBadges
) {
}

