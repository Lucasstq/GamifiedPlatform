package dev.gamified.GamifiedPlatform.mapper;

import dev.gamified.GamifiedPlatform.domain.Notification;
import dev.gamified.GamifiedPlatform.dtos.response.notification.NotificationResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationMapper {

    public static NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .referenceId(notification.getReferenceId())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }
}

