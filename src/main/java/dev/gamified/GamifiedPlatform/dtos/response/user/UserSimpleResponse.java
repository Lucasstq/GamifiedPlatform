package dev.gamified.GamifiedPlatform.dtos.response.user;

import lombok.Builder;

@Builder
public record UserSimpleResponse(String username,
                                 String avatarUrl,
                                 PlayerCharacterResponse character) {
}

