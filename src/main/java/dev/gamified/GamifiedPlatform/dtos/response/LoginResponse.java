package dev.gamified.GamifiedPlatform.dtos.response;

import lombok.Builder;

@Builder
public record LoginResponse(
        String token,
        String expiresIn
) {
}

