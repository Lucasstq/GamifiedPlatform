package dev.gamified.GamifiedPlatform.dtos.request;

import dev.gamified.GamifiedPlatform.enums.DifficultyLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LevelRequest(
        @NotNull(message = "Order level is required")
        @Min(value = 1, message = "Order level must be at least 1")
        Integer orderLevel,
        @NotBlank(message = "Name is required")
        @Size(max = 100, message = "Name must be less than 100 characters")
        String name,
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must be less than 200 characters")
        String title,
        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must be less than 1000 characters")
        String description,
        @NotNull(message = "XP required is required")
        @Min(value = 0, message = "XP required must be at least 0")
        Integer xpRequired,
        String iconUrl,
        @NotNull(message = "Difficulty level is required")
        DifficultyLevel difficultyLevel
) {
}
