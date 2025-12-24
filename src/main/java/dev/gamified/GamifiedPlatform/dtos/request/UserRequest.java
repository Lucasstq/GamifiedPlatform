package dev.gamified.GamifiedPlatform.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRequest(
        @NotBlank(message = "Username cannot be null")
        String username,
        @NotBlank(message = "Email cannot be null")
        String email,
        @NotBlank(message = "Password cannot be null")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,
        String avatarUrl) {
}
