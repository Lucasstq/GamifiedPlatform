package dev.gamified.GamifiedPlatform.dtos.request.mission;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record MissionUpdateRequest(
        @NotBlank(message = "Título é obrigatório")
        String title,
        @NotBlank(message = "Descrição é obrigatória")
        String description,
        @NotNull(message = "XP de recompensa é obrigatório")
        @Min(value = 1, message = "XP de recompensa deve ser maior que 0")
        Integer xpReward,
        @NotNull(message = "Ordem da missão é obrigatória")
        @Min(value = 1, message = "Ordem deve ser maior que 0")
        Integer orderNumber
) {
}
