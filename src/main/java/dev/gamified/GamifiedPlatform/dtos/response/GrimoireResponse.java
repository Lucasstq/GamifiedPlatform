package dev.gamified.GamifiedPlatform.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * DTO de resposta para grimórios.
 */
@Builder
public record GrimoireResponse(
        @JsonProperty("id")
        Long id,

        @JsonProperty("level_id")
        Long levelId,

        @JsonProperty("level_name")
        String levelName,

        @JsonProperty("level_order")
        Integer levelOrder,

        @JsonProperty("file_name")
        String fileName,

        @JsonProperty("original_name")
        String originalName,

        @JsonProperty("file_size")
        Long fileSize,

        @JsonProperty("description")
        String description,

        @JsonProperty("uploaded_at")
        LocalDateTime uploadedAt,

        @JsonProperty("is_accessible")
        Boolean isAccessible,

        @JsonProperty("download_count")
        Long downloadCount,

        @JsonProperty("user_downloaded")
        Boolean userDownloaded
) {
}

