package dev.gamified.GamifiedPlatform.dtos.response;

import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record MissionResponse(
        Long id,
        Long levelId,
        String levelName,
        String title,
        String description,
        Integer xpReward,
        Integer orderNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

}
