package dev.gamified.GamifiedPlatform.dtos.request.mission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record MissionSubmissionRequest(
        @NotBlank(message = "URL do GitHub é obrigatória")
        @Pattern(regexp = "^https://github\\.com/.*", message = "URL deve ser do GitHub")
        String submissionUrl,
        String submissionNotes
) {
}
