package dev.gamified.GamifiedPlatform.dtos.response.badges;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BadgeResponse(
        Long id,
        Long levelId,
        String levelName,
        Integer levelOrder,
        String name,
        String title,
        String description,
        String iconUrl,
        String rarity,
        LocalDateTime createdAt
) {
}
