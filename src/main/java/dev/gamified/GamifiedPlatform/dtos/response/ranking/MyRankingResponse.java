package dev.gamified.GamifiedPlatform.dtos.response.ranking;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

/**
 * DTO para resposta da posição do usuário no ranking.
 */
@Builder
public record MyRankingResponse(
        @JsonProperty("position")
        Long position,

        @JsonProperty("total_players")
        Long totalPlayers,

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

        @JsonProperty("percentile")
        Double percentile
) {
}

