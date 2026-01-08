package dev.gamified.GamifiedPlatform.dtos.request.mission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MissionEvaluationRequest(
        @NotNull(message = "Status de aprovação é obrigatório")
        Boolean approved,
        @NotBlank(message = "Feedback é obrigatório")
        String feedback
) {
}
