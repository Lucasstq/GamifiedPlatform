package dev.gamified.GamifiedPlatform.dtos.response.user;

import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserMissionResponse(
        Long id,
        Long missionId,
        String missionTitle,
        String missionDescription,
        Integer xpReward,
        Integer orderNumber,
        MissionStatus status,
        String submissionUrl,
        String submissionNotes,
        String feedback,
        String evaluatedByName,
        LocalDateTime startedAt,
        LocalDateTime submittedAt,
        LocalDateTime evaluatedAt,
        LocalDateTime completedAt
) {
}
