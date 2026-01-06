package dev.gamified.GamifiedPlatform.dtos.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRequest(
        @NotBlank(message = "Username cannot be null")
        String username,
        @NotBlank(message = "Email cannot be null")
        @Email(message = "Email should be valid")
        String email,
        @NotBlank(message = "Password cannot be null")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,
        String avatarUrl) {
}
