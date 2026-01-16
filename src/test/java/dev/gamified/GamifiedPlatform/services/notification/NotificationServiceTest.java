package dev.gamified.GamifiedPlatform.services.notification;

import dev.gamified.GamifiedPlatform.domain.Notification;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.enums.NotificationType;
import dev.gamified.GamifiedPlatform.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();
    }

    @Test
    @DisplayName("Deve criar notificação de missão avaliada corretamente")
    void shouldCreateMissionEvaluatedNotification() {
        notificationService.createMissionEvaluatedNotification(user, "Missao 1", true, 10L);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertEquals(user, notification.getUser());
        assertEquals(NotificationType.MISSION_EVALUATED, notification.getType());
        assertEquals("Missao Aprovada", notification.getTitle());
        assertTrue(notification.getMessage().contains("aprovada"));
        assertEquals(10L, notification.getReferenceId());
    }

    @Test
    @DisplayName("Deve criar notificação de level up corretamente")
    void shouldCreateLevelUpNotification() {
        notificationService.createLevelUpNotification(user, 2, "Intermediário");
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertEquals(NotificationType.LEVEL_UP, notification.getType());
        assertEquals("Level Up! Nivel 2", notification.getTitle());
        assertTrue(notification.getMessage().contains("alcancou o nivel 2"));
        assertEquals(2L, notification.getReferenceId());
    }

    @Test
    @DisplayName("Deve criar notificação de badge desbloqueado corretamente")
    void shouldCreateBadgeUnlockedNotification() {
        notificationService.createBadgeUnlockedNotification(user, "Badge X", "Descrição do badge", 5L);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertEquals(NotificationType.BADGE_UNLOCKED, notification.getType());
        assertEquals("Badge Desbloqueado", notification.getTitle());
        assertTrue(notification.getMessage().contains("Badge X"));
        assertEquals(5L, notification.getReferenceId());
    }

    @Test
    @DisplayName("Deve criar notificação de boss avaliado corretamente")
    void shouldCreateBossEvaluatedNotification() {
        notificationService.createBossEvaluatedNotification(user, "Boss Y", false, 7L);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertEquals(NotificationType.BOSS_EVALUATED, notification.getType());
        assertEquals("Boss Nao Derrotado", notification.getTitle());
        assertTrue(notification.getMessage().contains("falhou"));
        assertEquals(7L, notification.getReferenceId());
    }

    @Test
    @DisplayName("Deve criar notificação de boss desbloqueado corretamente")
    void shouldCreateBossUnlockedNotification() {
        notificationService.createBossUnlockedNotification(user, "Boss Z", "Level 3", 8L);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertEquals(NotificationType.BOSS_UNLOCKED, notification.getType());
        assertEquals("Novo Boss Desbloqueado", notification.getTitle());
        assertTrue(notification.getMessage().contains("Boss Z"));
        assertEquals(8L, notification.getReferenceId());
    }

    @Test
    @DisplayName("Deve criar notificação de grimório desbloqueado corretamente")
    void shouldCreateGrimoireUnlockedNotification() {
        notificationService.createGrimoireUnlockedNotification(user, "Level 4", 9L);
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        Notification notification = captor.getValue();
        assertEquals(NotificationType.GRIMOIRE_UNLOCKED, notification.getType());
        assertEquals("Grimorio Desbloqueado", notification.getTitle());
        assertTrue(notification.getMessage().contains("Level 4"));
        assertEquals(9L, notification.getReferenceId());
    }
}

