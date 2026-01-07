package dev.gamified.GamifiedPlatform.enums;

import lombok.Getter;

@Getter
public enum DifficultyLevel {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    EXPERT("Expert");

    private String description;

    private DifficultyLevel(String description) {
        this.description = description;

    }

}
