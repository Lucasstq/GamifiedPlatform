package dev.gamified.GamifiedPlatform.dtos.request.user;

import jakarta.validation.constraints.Email;
import lombok.Builder;

@Builder
public record UserForgetPasswordRequest(
        @Email(message = "Email should be valid")
        String email) {
}
