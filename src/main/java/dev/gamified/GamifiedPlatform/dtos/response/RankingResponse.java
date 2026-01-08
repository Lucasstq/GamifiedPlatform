package dev.gamified.GamifiedPlatform.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * DTO para resposta de ranking global.
 * Representa a posição de um jogador no ranking.
 */
@Builder
public record RankingResponse(
        @JsonProperty("position")
        Integer position,

        @JsonProperty("user_id")
        Long userId,

        @JsonProperty("username")
        String username,

        @JsonProperty("character_name")
        String characterName,

        @JsonProperty("level")
        Integer level,

        @JsonProperty("xp")
        Integer xp,

        @JsonProperty("level_name")
        String levelName,

        @JsonProperty("level_title")
        String levelTitle,

        @JsonProperty("is_me")
        Boolean isMe
) {
}
