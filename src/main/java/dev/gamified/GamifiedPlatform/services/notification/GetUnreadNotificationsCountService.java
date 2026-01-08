package dev.gamified.GamifiedPlatform.services.notification;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUnreadNotificationsCountService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Long execute() {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated"));

        Long count = notificationRepository.countUnreadByUserId(userId);
        log.debug("User {} has {} unread notifications", userId, count);

        return count;
    }
}

