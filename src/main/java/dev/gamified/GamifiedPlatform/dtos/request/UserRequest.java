package dev.gamified.GamifiedPlatform.dtos.request;

import lombok.Builder;

@Builder
public record UserRequest(
        String username,
        String email,
        String password,
        String avatarUrl) {
}
