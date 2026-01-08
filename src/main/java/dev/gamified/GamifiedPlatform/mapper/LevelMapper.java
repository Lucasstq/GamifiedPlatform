package dev.gamified.GamifiedPlatform.mapper;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.request.level.LevelRequest;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public class LevelMapper {

    public static Levels toEntity(LevelRequest request) {
        return Levels.builder()
                .orderLevel(request.orderLevel())
                .name(request.name())
                .title(request.title())
                .description(request.description())
                .xpRequired(request.xpRequired())
                .iconUrl(request.iconUrl())
                .difficultyLevel(request.difficultyLevel())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static LevelResponse toResponse(Levels level) {
        return LevelResponse.builder()
                .id(level.getId())
                .orderLevel(level.getOrderLevel())
                .name(level.getName())
                .title(level.getTitle())
                .description(level.getDescription())
                .xpRequired(level.getXpRequired())
                .iconUrl(level.getIconUrl())
                .difficultyLevel(level.getDifficultyLevel())
                .createdAt(level.getCreatedAt())
                .updatedAt(level.getUpdatedAt())
                .build();
    }

    public static void updateEntityFromRequest(Levels level, LevelRequest request) {
        level.setOrderLevel(request.orderLevel());
        level.setName(request.name());
        level.setTitle(request.title());
        level.setDescription(request.description());
        level.setXpRequired(request.xpRequired());
        level.setIconUrl(request.iconUrl());
        level.setDifficultyLevel(request.difficultyLevel());
        level.setUpdatedAt(LocalDateTime.now());
    }
}

