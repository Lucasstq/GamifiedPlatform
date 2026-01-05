package dev.gamified.GamifiedPlatform.dtos.response;

import dev.gamified.GamifiedPlatform.enums.DifficutyLevel;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LevelResponse(
        Long id,
        Integer orderLevel,
        String name,
        String title,
        String description,
        Integer xpRequired,
        String iconUrl,
        DifficutyLevel difficultyLevel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
