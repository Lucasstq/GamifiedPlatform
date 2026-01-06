package dev.gamified.GamifiedPlatform.dtos.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserChangePasswordRequest(
        @NotBlank(message = "Current password cannot be null")
        String currentPassword,
        @NotBlank(message = "New password cannot be null")
        @Size(min = 8, message = "New password must be at least 8 characters long")
        String newPassword,
        @NotBlank(message = "Confirm new password cannot be null")
        String confirmNewPassword
) {
}
