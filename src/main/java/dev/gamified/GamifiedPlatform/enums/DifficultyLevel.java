package dev.gamified.GamifiedPlatform.enums;

public enum DifficutyLevel {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
    EXPERT("Expert");

    private String description;

    private DifficutyLevel(String description) {
        this.description = description;

    }

}
