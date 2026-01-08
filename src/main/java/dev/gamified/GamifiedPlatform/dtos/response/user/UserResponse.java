package dev.gamified.GamifiedPlatform.dtos.response.user;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserResponse(
        Long id,
        String username,
        String email,
        String avatarUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
