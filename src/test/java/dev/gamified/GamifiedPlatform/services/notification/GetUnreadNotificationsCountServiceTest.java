package dev.gamified.GamifiedPlatform.services.notification;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUnreadNotificationsCountServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    private GetUnreadNotificationsCountService service;

    @BeforeEach
    void setUp() {
        service = new GetUnreadNotificationsCountService(notificationRepository);
    }

    @Test
    @DisplayName("Deve retornar a quantidade de notificações não lidas do usuário autenticado")
    void execute_shouldReturnUnreadCount_whenUserAuthenticated() {
        Long userId = 42L;

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.of(userId));

            when(notificationRepository.countUnreadByUserId(userId))
                    .thenReturn(5L);

            Long result = service.execute();

            assertEquals(5L, result);
            verify(notificationRepository).countUnreadByUserId(userId);
        }
    }

    @Test
    @DisplayName("Deve lançar AccessDeniedException quando usuário não estiver autenticado")
    void execute_shouldThrowAccessDenied_whenUserNotAuthenticated() {
        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId)
                    .thenReturn(Optional.empty());

            assertThrows(AccessDeniedException.class,
                    () -> service.execute());
        }

        verifyNoInteractions(notificationRepository);
    }
}

