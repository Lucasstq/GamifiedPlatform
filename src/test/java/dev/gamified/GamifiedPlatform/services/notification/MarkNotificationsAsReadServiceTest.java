package dev.gamified.GamifiedPlatform.services.notification;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Notification;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.enums.NotificationType;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkNotificationsAsReadServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    private MarkNotificationsAsReadService service;

    @BeforeEach
    void setUp() {
        service = new MarkNotificationsAsReadService(notificationRepository);
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao marcar todas como lidas quando usuário não estiver autenticado")
    void markAllAsRead_shouldThrowAccessDenied_whenNotAuthenticated() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.empty());

            assertThrows(AccessDeniedException.class, () -> service.markAllAsRead());
        }

        verifyNoInteractions(notificationRepository);
    }

    @Test
    @DisplayName("Deve marcar todas as notificações como lidas chamando update em massa no repositório")
    void markAllAsRead_shouldCallRepositoryBulkUpdate_whenAuthenticated() {
        Long userId = 10L;

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            service.markAllAsRead();

            ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
            verify(notificationRepository).markAllAsReadByUserId(eq(userId), timeCaptor.capture());

            assertNotNull(timeCaptor.getValue());
        }
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException ao marcar uma notificação como lida quando usuário não estiver autenticado")
    void markAsRead_shouldThrowAccessDenied_whenNotAuthenticated() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.empty());

            assertThrows(AccessDeniedException.class, () -> service.markAsRead(1L));
        }

        verifyNoInteractions(notificationRepository);
    }

    @Test
    @DisplayName("Não deve fazer nada quando notificação não existir")
    void markAsRead_shouldDoNothing_whenNotificationNotFound() {
        Long userId = 10L;
        Long notificationId = 99L;

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            service.markAsRead(notificationId);
        }

        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando tentar marcar como lida notificação de outro usuário")
    void markAsRead_shouldThrowAccessDenied_whenNotificationFromOtherUser() {
        Long userId = 10L;
        Long notificationId = 1L;

        Notification notification = notification(notificationId, 999L, false);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            assertThrows(AccessDeniedException.class, () -> service.markAsRead(notificationId));
        }

        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve marcar notificação como lida quando for do próprio usuário e ainda não estiver lida")
    void markAsRead_shouldMarkAsReadAndSave_whenOwnNotificationAndUnread() {
        Long userId = 10L;
        Long notificationId = 1L;

        Notification notification = notification(notificationId, userId, false);
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            service.markAsRead(notificationId);
        }

        assertTrue(notification.getIsRead());
        assertNotNull(notification.getReadAt());

        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository).save(notification);
    }

    @Test
    @DisplayName("Não deve salvar quando notificação já estiver lida")
    void markAsRead_shouldNotSave_whenAlreadyRead() {
        Long userId = 10L;
        Long notificationId = 1L;

        Notification notification = notification(notificationId, userId, true);
        notification.setReadAt(LocalDateTime.now().minusDays(1));

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(notification));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            service.markAsRead(notificationId);
        }

        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository, never()).save(any());
    }

    private Notification notification(Long notificationId, Long ownerUserId, boolean isRead) {
        User user = new User();
        user.setId(ownerUserId);
        user.setUsername("user-" + ownerUserId);

        Notification n = new Notification();
        n.setId(notificationId);
        n.setUser(user);
        n.setType(NotificationType.SYSTEM);
        n.setTitle("Title");
        n.setMessage("Message");
        n.setIsRead(isRead);
        n.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        n.setReadAt(isRead ? LocalDateTime.now().minusMinutes(1) : null);
        return n;
    }
}

