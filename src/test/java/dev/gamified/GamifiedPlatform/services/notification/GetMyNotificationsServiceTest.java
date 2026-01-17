package dev.gamified.GamifiedPlatform.services.notification;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Notification;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.notification.NotificationResponse;
import dev.gamified.GamifiedPlatform.enums.NotificationType;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMyNotificationsServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    private GetMyNotificationsService service;

    @BeforeEach
    void setUp() {
        service = new GetMyNotificationsService(notificationRepository);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário não estiver autenticado")
    void execute_shouldThrowAccessDenied_whenNotAuthenticated() {
        Pageable pageable = PageRequest.of(0, 10);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.empty());

            assertThrows(AccessDeniedException.class,
                    () -> service.execute(pageable, false));
        }

        verifyNoInteractions(notificationRepository);
    }

    @Test
    @DisplayName("Deve buscar apenas notificações não lidas quando onlyUnread for true")
    void execute_shouldFetchUnreadNotifications_whenOnlyUnreadTrue() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 42L;

        Page<Notification> unread = new PageImpl<>(
                List.of(notification(1L, userId, false), notification(2L, userId, false)),
                pageable,
                2
        );

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(notificationRepository.findUnreadByUserId(userId, pageable)).thenReturn(unread);

            Page<NotificationResponse> result = service.execute(pageable, true);

            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            assertEquals(2, result.getContent().size());

            verify(notificationRepository).findUnreadByUserId(userId, pageable);
            verify(notificationRepository, never()).findByUserId(anyLong(), any());
        }
    }

    @Test
    @DisplayName("Deve buscar todas as notificações quando onlyUnread for false")
    void execute_shouldFetchAllNotifications_whenOnlyUnreadFalse() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 42L;

        Page<Notification> all = new PageImpl<>(
                List.of(
                        notification(1L, userId, false),
                        notification(2L, userId, true),
                        notification(3L, userId, false)
                ),
                pageable,
                3
        );

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(notificationRepository.findByUserId(userId, pageable)).thenReturn(all);

            Page<NotificationResponse> result = service.execute(pageable, false);

            assertNotNull(result);
            assertEquals(3, result.getTotalElements());
            assertEquals(3, result.getContent().size());

            verify(notificationRepository).findByUserId(userId, pageable);
            verify(notificationRepository, never()).findUnreadByUserId(anyLong(), any());
        }
    }

    @Test
    @DisplayName("Deve buscar todas as notificações quando onlyUnread for null")
    void execute_shouldFetchAllNotifications_whenOnlyUnreadNull() {
        Pageable pageable = PageRequest.of(0, 10);
        Long userId = 42L;

        Page<Notification> all = new PageImpl<>(
                List.of(notification(1L, userId, false)),
                pageable,
                1
        );

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(notificationRepository.findByUserId(userId, pageable)).thenReturn(all);

            Page<NotificationResponse> result = service.execute(pageable, null);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals(1, result.getContent().size());

            verify(notificationRepository).findByUserId(userId, pageable);
            verify(notificationRepository, never()).findUnreadByUserId(anyLong(), any());
        }
    }

    private Notification notification(Long notificationId, Long userId, boolean isRead) {
        User user = new User();
        user.setId(userId);
        user.setUsername("user-" + userId);

        Notification n = new Notification();
        n.setId(notificationId);
        n.setUser(user);
        n.setType(NotificationType.SYSTEM);
        n.setTitle("Title " + notificationId);
        n.setMessage("Message " + notificationId);
        n.setIsRead(isRead);
        n.setCreatedAt(LocalDateTime.now().minusMinutes(notificationId));
        n.setReadAt(isRead ? LocalDateTime.now() : null);
        n.setReferenceId(123L);

        return n;
    }
}
