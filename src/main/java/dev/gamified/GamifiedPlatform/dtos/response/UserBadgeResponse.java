package dev.gamified.GamifiedPlatform.dtos.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserBadgeResponse(
        Long id,
        Long userId,
        String username,
        BadgeResponse badge,
        LocalDateTime unlockedAt,
        Long unlockedByBossId,
        String unlockedByBossName
) {
}

