package dev.gamified.GamifiedPlatform.enums;

import lombok.Getter;

@Getter
public enum Roles {
    ROLE_USER("Student"),
    ROLE_ADMIN("Administrator"),
    ROLE_MENTOR("Mentor");
    private String description;

    Roles(String description) {
        this.description = description;
    }


}

