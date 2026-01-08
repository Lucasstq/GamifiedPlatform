package dev.gamified.GamifiedPlatform.controller.notification;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadProfile;
import dev.gamified.GamifiedPlatform.dtos.response.notification.NotificationResponse;
import dev.gamified.GamifiedPlatform.services.notification.GetMyNotificationsService;
import dev.gamified.GamifiedPlatform.services.notification.GetUnreadNotificationsCountService;
import dev.gamified.GamifiedPlatform.services.notification.MarkNotificationsAsReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final GetMyNotificationsService getMyNotificationsService;
    private final MarkNotificationsAsReadService markNotificationsAsReadService;
    private final GetUnreadNotificationsCountService getUnreadNotificationsCountService;

    @GetMapping
    @CanReadProfile
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyUnread) {
        return ResponseEntity.ok(getMyNotificationsService.execute(pageable, onlyUnread));
    }

    @GetMapping("/unread/count")
    @CanReadProfile
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(getUnreadNotificationsCountService.execute());
    }

    @PutMapping("/{notificationId}/read")
    @CanReadProfile
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        markNotificationsAsReadService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read-all")
    @CanReadProfile
    public ResponseEntity<Void> markAllAsRead() {
        markNotificationsAsReadService.markAllAsRead();
        return ResponseEntity.noContent().build();
    }
}

