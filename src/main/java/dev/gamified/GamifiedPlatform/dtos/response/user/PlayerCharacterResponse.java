package dev.gamified.GamifiedPlatform.dtos.response.user;

import lombok.Builder;

@Builder
public record PlayerCharacterResponse(
        String name,
        int level,
        int xp) {
}
