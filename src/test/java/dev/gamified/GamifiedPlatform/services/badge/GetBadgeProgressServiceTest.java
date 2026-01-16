package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.config.security.PermissionValidator;
import dev.gamified.GamifiedPlatform.dtos.response.badges.BadgeProgressResponse;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.BadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserBadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetBadgeProgressService Tests")
class GetBadgeProgressServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetBadgeProgressService getBadgeProgressService;

    @Test
    @DisplayName("Deve retornar progresso do badge para usuário existente")
    void shouldReturnBadgeProgressForUser() {

        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        when(badgeRepository.count()).thenReturn(10L);
        when(userBadgeRepository.countByUserId(userId)).thenReturn(4L);

        try (MockedStatic<PermissionValidator> mockedPermission =
                     mockStatic(PermissionValidator.class)) {

            mockedPermission
                    .when(() -> PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenAnswer(invocation -> null);

            BadgeProgressResponse response =
                    getBadgeProgressService.execute(userId);

            assertNotNull(response);
            assertEquals(10L, response.totalBadges());
            assertEquals(4L, response.unlockedBadges());
            assertEquals(6L, response.remainingBadges());
            assertTrue(response.progressPercentage() > 0);
        }
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não existir")
    void shouldThrowIfUserNotFound() {

        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> getBadgeProgressService.execute(userId)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção de permissão se usuário não autorizado")
    void shouldThrowAccessDeniedIfNoPermission() {

        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        try (MockedStatic<PermissionValidator> mockedPermission =
                     mockStatic(PermissionValidator.class)) {

            mockedPermission
                    .when(() -> PermissionValidator.validateResourceOwnerOrAdmin(userId))
                    .thenThrow(new AccessDeniedException(
                            "You do not have permission to access this resource"));

            assertThrows(
                    AccessDeniedException.class,
                    () -> getBadgeProgressService.execute(userId)
            );
        }
    }
}
