package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.config.security.PermissionValidator;
import dev.gamified.GamifiedPlatform.domain.Badge;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserBadge;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserBadgeResponse;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.UserBadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserBadgesService Tests")
class GetUserBadgesServiceTest {

    @Mock
    private UserBadgeRepository userBadgeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserBadgesService getUserBadgesService;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Deve retornar badges paginados do usuário")
    void shouldReturnPagedUserBadges() {

        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        var level = dev.gamified.GamifiedPlatform.domain.Levels.builder()
                .id(1L)
                .name("Level 1")
                .orderLevel(1)
                .title("Título")
                .description("Desc")
                .xpRequired(100)
                .iconUrl("icon.png")
                .difficultyLevel(null)
                .build();

        var badge = Badge.builder()
                .id(1L)
                .name("Badge Teste")
                .title("Título")
                .description("Desc")
                .iconUrl("icon.png")
                .level(level)
                .build();

        var user = User.builder()
                .id(userId)
                .username("user")
                .build();

        var userBadge = UserBadge.builder()
                .id(1L)
                .user(user)
                .badge(badge)
                .build();

        when(userBadgeRepository.findAllByUserId(userId))
                .thenReturn(List.of(userBadge));

        try (MockedStatic<PermissionValidator> mockedPermission =
                     mockStatic(PermissionValidator.class)) {

            mockedPermission
                    .when(() -> PermissionValidator
                            .validateResourceOwnerAdminOrMentor(userId))
                    .thenAnswer(invocation -> null);

            Page<UserBadgeResponse> page =
                    getUserBadgesService.execute(userId, pageable);

            assertNotNull(page);
            assertEquals(1, page.getTotalElements());
            assertEquals(1, page.getContent().size());

            UserBadgeResponse response = page.getContent().get(0);

            assertEquals("user", response.username());
            assertEquals("Badge Teste", response.badge().name());
        }
    }

    @Test
    @DisplayName("Deve lançar exceção se usuário não existir")
    void shouldThrowIfUserNotFound() {

        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        try (MockedStatic<PermissionValidator> mockedPermission =
                     mockStatic(PermissionValidator.class)) {

            mockedPermission
                    .when(() -> PermissionValidator
                            .validateResourceOwnerAdminOrMentor(userId))
                    .thenAnswer(invocation -> null);

            assertThrows(ResourceNotFoundException.class,
                    () -> getUserBadgesService.execute(userId, pageable));
        }
    }

    @Test
    @DisplayName("Deve lançar exceção de permissão se usuário não autorizado")
    void shouldThrowAccessDeniedIfNoPermission() {

        Long userId = 1L;

        try (MockedStatic<PermissionValidator> mockedPermission =
                     mockStatic(PermissionValidator.class)) {

            mockedPermission
                    .when(() -> PermissionValidator
                            .validateResourceOwnerAdminOrMentor(userId))
                    .thenThrow(new dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException(
                            "You do not have permission to access this resource"));

            assertThrows(
                    AccessDeniedException.class,
                    () -> getUserBadgesService.execute(userId, pageable)
            );
        }
    }

}
