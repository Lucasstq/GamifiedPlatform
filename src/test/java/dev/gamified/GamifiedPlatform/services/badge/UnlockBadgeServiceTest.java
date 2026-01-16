package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.domain.Badge;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserBadge;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserBadgeResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.BadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserBadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UnlockBadgeService Tests")
class UnlockBadgeServiceTest {

    @Mock
    private UserBadgeRepository userBadgeRepository;
    @Mock
    private BadgeRepository badgeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private NotificationService notificationService;
    @InjectMocks
    private UnlockBadgeService unlockBadgeService;

    private User user;
    private Badge badge;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("user").build();
        Levels level = dev.gamified.GamifiedPlatform.domain.Levels.builder()
            .id(3L)
            .orderLevel(1)
            .name("Level 1")
            .title("Título")
            .description("Descrição")
            .xpRequired(100)
            .iconUrl("icon.png")
            .difficultyLevel(null)
            .build();
        badge = Badge.builder().id(2L).name("Badge Teste").description("Desc").level(level).title("Título Badge").build();
    }

    @Test
    @DisplayName("Deve desbloquear badge com sucesso")
    void shouldUnlockBadgeSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(badgeRepository.findByLevelId(3L)).thenReturn(Optional.of(badge));
        when(userBadgeRepository.existsByUserIdAndBadgeId(1L, 2L)).thenReturn(false);
        when(userBadgeRepository.save(any(UserBadge.class))).thenAnswer(i -> {
            UserBadge ub = i.getArgument(0);
            ub.setId(10L); // Simula ID gerado
            return ub;
        });

        UserBadgeResponse response = unlockBadgeService.execute(1L, 3L, 4L);
        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(1L, response.userId());
        assertEquals("user", response.username());
        assertNotNull(response.badge());
        assertEquals(2L, response.badge().id());
        assertEquals(3L, response.badge().levelId());
        verify(notificationService).createBadgeUnlockedNotification(user, badge.getName(), badge.getDescription(), badge.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não encontrado")
    void shouldThrowIfUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> unlockBadgeService.execute(1L, 3L, 4L));
    }

    @Test
    @DisplayName("Deve lançar exceção se badge não encontrado")
    void shouldThrowIfBadgeNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(badgeRepository.findByLevelId(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> unlockBadgeService.execute(1L, 3L, 4L));
    }

    @Test
    @DisplayName("Deve lançar exceção se badge já desbloqueado")
    void shouldThrowIfBadgeAlreadyUnlocked() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(badgeRepository.findByLevelId(3L)).thenReturn(Optional.of(badge));
        when(userBadgeRepository.existsByUserIdAndBadgeId(1L, 2L)).thenReturn(true);
        assertThrows(BusinessException.class, () -> unlockBadgeService.execute(1L, 3L, 4L));
    }
}
