package dev.gamified.GamifiedPlatform.dtos.request.boss;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record BossFightSubmissionRequest(
        @NotBlank(message = "URL do GitHub é obrigatória")
        @Pattern(regexp = "^https://github\\.com/.*", message = "URL deve ser do GitHub")
        String submissionUrl,
        String submissionNotes
) {
}

