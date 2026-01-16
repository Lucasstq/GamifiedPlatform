package dev.gamified.GamifiedPlatform.services.boss;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.*;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.BossProgressResponse;
import dev.gamified.GamifiedPlatform.enums.BossFightStatus;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckBossUnlockService Tests")
class CheckBossUnlockServiceTest {

    @Mock
    private UserMissionRepository userMissionRepository;

    @Mock
    private BossRepository bossRepository;

    @Mock
    private UserBossRepository userBossRepository;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CheckBossUnlockService checkBossUnlockService;

    private User user;
    private Levels level;
    private Boss boss;

    @BeforeEach
    void setUp() {
        level = new Levels();
        level.setId(1L);
        level.setName("Iniciante");
        level.setOrderLevel(1);

        boss = Boss.builder()
                .id(1L)
                .level(level)
                .name("Syntax Sentinel")
                .title("Guardião da Sintaxe")
                .xpReward(150)
                .build();

        user = User.builder()
                .id(1L)
                .username("testuser")
                .build();
    }

    @Test
    @DisplayName("Deve desbloquear o chefe quando o progresso atingir 80%")
    void shouldUnlockBossWhenProgressReaches80Percent() {
        // Arrange
        Long levelId = 1L;
        Long userId = 1L;

        UserBoss userBoss = UserBoss.builder()
                .id(1L)
                .user(user)
                .boss(boss)
                .status(BossFightStatus.LOCKED)
                .build();

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
            when(bossRepository.findByLevelId(levelId)).thenReturn(Optional.of(boss));
            when(missionRepository.countByLevelId(levelId)).thenReturn(10L);
            when(userMissionRepository.countCompletedMissionsByUserAndLevel(userId, levelId)).thenReturn(8L);
            when(userBossRepository.findByUserIdAndBossId(userId, boss.getId())).thenReturn(Optional.of(userBoss));
            when(userBossRepository.save(any(UserBoss.class))).thenReturn(userBoss);

            // Act
            BossProgressResponse result = checkBossUnlockService.execute(levelId);

            // Assert
            assertNotNull(result);
            assertEquals(80.0, result.progressPercentage());
            assertTrue(result.bossUnlocked());
            assertEquals(BossFightStatus.UNLOCKED, userBoss.getStatus());
            assertNotNull(userBoss.getUnlockedAt());

            verify(userBossRepository).save(any(UserBoss.class));
        }
    }

    @Test
    @DisplayName("Não se deve desbloquear o chefe quando o progresso estiver abaixo de 80%.")
    void shouldNotUnlockBossWhenProgressIsBelow80Percent() {
        // Arrange
        Long levelId = 1L;
        Long userId = 1L;

        UserBoss userBoss = UserBoss.builder()
                .id(1L)
                .user(user)
                .boss(boss)
                .status(BossFightStatus.LOCKED)
                .build();

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
            when(bossRepository.findByLevelId(levelId)).thenReturn(Optional.of(boss));
            when(missionRepository.countByLevelId(levelId)).thenReturn(10L);
            when(userMissionRepository.countCompletedMissionsByUserAndLevel(userId, levelId)).thenReturn(5L); // 50%
            when(userBossRepository.findByUserIdAndBossId(userId, boss.getId())).thenReturn(Optional.of(userBoss));

            // Act
            BossProgressResponse result = checkBossUnlockService.execute(levelId);

            // Assert
            assertNotNull(result);
            assertEquals(50.0, result.progressPercentage());
            assertFalse(result.bossUnlocked());
            assertEquals(BossFightStatus.LOCKED, userBoss.getStatus());
            assertNull(userBoss.getUnlockedAt());

            verify(userBossRepository, never()).save(any(UserBoss.class));
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando o nível não for encontrado")
    void shouldThrowExceptionWhenLevelNotFound() {
        // Arrange
        Long levelId = 999L;
        Long userId = 1L;

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(levelRepository.findById(levelId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> checkBossUnlockService.execute(levelId));
        }
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando o chefe não for encontrado")
    void shouldThrowExceptionWhenBossNotFound() {
        // Arrange
        Long levelId = 1L;
        Long userId = 1L;

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
            when(bossRepository.findByLevelId(levelId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> checkBossUnlockService.execute(levelId));
        }
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando não houver missões no nível")
    void shouldThrowExceptionWhenNoMissionsInLevel() {
        // Arrange
        Long levelId = 1L;
        Long userId = 1L;

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
            when(bossRepository.findByLevelId(levelId)).thenReturn(Optional.of(boss));
            when(missionRepository.countByLevelId(levelId)).thenReturn(0L);

            // Act & Assert
            assertThrows(BusinessException.class, () -> checkBossUnlockService.execute(levelId));
        }
    }

    @Test
    @DisplayName("Deve criar um novo UserBoss se não existir")
    void shouldCreateNewUserBossIfNotExists() {
        // Arrange
        Long levelId = 1L;
        Long userId = 1L;

        UserBoss newUserBoss = UserBoss.builder()
                .user(user)
                .boss(boss)
                .status(BossFightStatus.LOCKED)
                .build();

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
            when(bossRepository.findByLevelId(levelId)).thenReturn(Optional.of(boss));
            when(missionRepository.countByLevelId(levelId)).thenReturn(10L);
            when(userMissionRepository.countCompletedMissionsByUserAndLevel(userId, levelId)).thenReturn(5L);
            when(userBossRepository.findByUserIdAndBossId(userId, boss.getId())).thenReturn(Optional.empty());
            when(userBossRepository.save(any(UserBoss.class))).thenReturn(newUserBoss);

            // Act
            BossProgressResponse result = checkBossUnlockService.execute(levelId);

            // Assert
            assertNotNull(result);
            verify(userBossRepository, times(1)).save(any(UserBoss.class)); // Save apenas para criar
        }
    }
}

