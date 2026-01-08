package dev.gamified.GamifiedPlatform.dtos.request.boss;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BossFightEvaluationRequest(
        @NotNull(message = "Status de aprovação é obrigatório")
        Boolean approved,
        @NotBlank(message = "Feedback é obrigatório")
        String feedback
) {
}

