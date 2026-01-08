package dev.gamified.GamifiedPlatform.dtos.request.boss;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BossCreateRequest(
        @NotNull(message = "Level ID é obrigatório")
        Long levelId,

        @NotBlank(message = "Nome do boss é obrigatório")
        String name,

        @NotBlank(message = "Título do boss é obrigatório")
        String title,

        @NotBlank(message = "Descrição do boss é obrigatória")
        String description,

        @NotBlank(message = "Desafio do boss é obrigatório")
        String challenge,

        @NotNull(message = "XP de recompensa é obrigatório")
        @Min(value = 1, message = "XP de recompensa deve ser maior que 0")
        Integer xpReward,

        @NotBlank(message = "Nome da badge é obrigatório")
        String badgeName,

        @NotBlank(message = "Descrição da badge é obrigatória")
        String badgeDescription,

        String imageUrl,

        String badgeIconUrl,

        Boolean unlocksNextLevel
) {
}

