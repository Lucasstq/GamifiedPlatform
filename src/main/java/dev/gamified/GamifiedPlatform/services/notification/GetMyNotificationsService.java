package dev.gamified.GamifiedPlatform.services.notification;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.dtos.response.notification.NotificationResponse;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.mapper.NotificationMapper;
import dev.gamified.GamifiedPlatform.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetMyNotificationsService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> execute(Pageable pageable, Boolean onlyUnread) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated"));

        log.info("Fetching notifications for user {} - onlyUnread: {}", userId, onlyUnread);

        if (Boolean.TRUE.equals(onlyUnread)) {
            return notificationRepository.findUnreadByUserId(userId, pageable)
                    .map(NotificationMapper::toResponse);
        } else {
            return notificationRepository.findByUserId(userId, pageable)
                    .map(NotificationMapper::toResponse);
        }
    }
}

