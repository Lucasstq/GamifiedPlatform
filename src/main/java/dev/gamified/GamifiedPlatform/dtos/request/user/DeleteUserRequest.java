package dev.gamified.GamifiedPlatform.dtos.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record DeleteUserRequest(
        @NotBlank(message = "Password is required to delete account")
        String password
) {
}

