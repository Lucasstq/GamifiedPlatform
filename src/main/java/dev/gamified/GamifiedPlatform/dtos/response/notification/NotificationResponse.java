package dev.gamified.GamifiedPlatform.dtos.response.notification;

import dev.gamified.GamifiedPlatform.enums.NotificationType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NotificationResponse(
        Long id,
        NotificationType type,
        String title,
        String message,
        Boolean isRead,
        Long referenceId,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
}
