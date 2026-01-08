package dev.gamified.GamifiedPlatform.services.notification;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MarkNotificationsAsReadService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void markAllAsRead() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated"));

        log.info("Marking all notifications as read for user {}", userId);

        notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated"));

        notificationRepository.findById(notificationId).ifPresent(notification -> {
            if (!notification.getUser().getId().equals(userId)) {
                throw new AccessDeniedException("You can only mark your own notifications as read");
            }

            if (!notification.getIsRead()) {
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
                log.info("Notification {} marked as read for user {}", notificationId, userId);
            }
        });
    }
}

