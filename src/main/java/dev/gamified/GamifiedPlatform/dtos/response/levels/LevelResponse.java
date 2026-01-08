package dev.gamified.GamifiedPlatform.dtos.response.levels;

import dev.gamified.GamifiedPlatform.enums.DifficultyLevel;
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
        DifficultyLevel difficultyLevel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
