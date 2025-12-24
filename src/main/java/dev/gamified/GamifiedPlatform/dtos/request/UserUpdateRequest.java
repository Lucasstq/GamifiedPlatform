package dev.gamified.GamifiedPlatform.dtos.request;

import lombok.Builder;

@Builder
public record UserUpdateRequest(
        String username,
        String email,
        String password,
        String avatarUrl
) {
}
