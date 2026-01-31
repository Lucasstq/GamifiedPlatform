package dev.gamified.GamifiedPlatform.services.ranking;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.MyRankingResponse;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.RankingInfo;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetMyRankingService Tests")
class GetMyRankingServiceTest {

    @Mock
    private PlayerCharacterRepository playerCharacterRepository;

    @Mock
    private LevelRepository levelRepository;

    @InjectMocks
    private GetMyRankingService getMyRankingService;

    private PlayerCharacter playerCharacter;
    private Levels level;

    @BeforeEach
    void setUp() {
        level = new Levels();
        level.setId(3L);
        level.setOrderLevel(3);
        level.setName("Praticante Dedicado");
        level.setTitle("Conhecedor das Estruturas");

        playerCharacter = PlayerCharacter.builder()
                .id(1L)
                .name("Hero")
                .level(3)
                .xp(600)
                .build();
    }

    @Test
    @DisplayName("Deve retornar a posição de classificação do usuário com sucesso.")
    void shouldReturnUserRankingPositionSuccessfully() {
        // Arrange
        Long userId = 1L;
        RankingInfo rankingInfo = new RankingInfo(5L, 100L);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(playerCharacterRepository.findByUserId(userId)).thenReturn(Optional.of(playerCharacter));
            when(playerCharacterRepository.findPlayerPosition(playerCharacter.getId())).thenReturn(rankingInfo);
            when(levelRepository.findTopByOrderLevelLessThanEqualOrderByOrderLevelDesc(anyInt()))
                    .thenReturn(Optional.of(level));

            // Act
            MyRankingResponse result = getMyRankingService.execute();

            // Assert
            assertNotNull(result);
            assertEquals(5L, result.position());
            assertEquals(100L, result.totalPlayers());
            assertEquals("Hero", result.characterName());
            assertEquals(3, result.level());
            assertEquals(600, result.xp());
            assertEquals("Praticante Dedicado", result.levelName());
            assertTrue(result.percentile() > 0);

            verify(playerCharacterRepository).findByUserId(userId);
            verify(playerCharacterRepository).findPlayerPosition(playerCharacter.getId());
        }
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando o usuário não estiver autenticado.")
    void shouldThrowExceptionWhenUserNotAuthenticated() {
        // Arrange
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> getMyRankingService.execute());

            verify(playerCharacterRepository, never()).findByUserId(any());
        }
    }

    @Test
    @DisplayName("Deve lançar uma exceção quando o personagem do usuário não for encontrado.")
    void shouldThrowExceptionWhenCharacterNotFound() {
        // Arrange
        Long userId = 1L;

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(playerCharacterRepository.findByUserId(userId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> getMyRankingService.execute());

            verify(playerCharacterRepository).findByUserId(userId);
            verify(playerCharacterRepository, never()).findPlayerPosition(any());
        }
    }

    @Test
    @DisplayName("Deve lidar com cenário onde nenhum nível é encontrado.")
    void shouldHandleRankingWithNoLevelFound() {
        // Arrange
        Long userId = 1L;
        RankingInfo rankingInfo = new RankingInfo(1L, 10L);

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(playerCharacterRepository.findByUserId(userId)).thenReturn(Optional.of(playerCharacter));
            when(playerCharacterRepository.findPlayerPosition(playerCharacter.getId())).thenReturn(rankingInfo);
            when(levelRepository.findTopByOrderLevelLessThanEqualOrderByOrderLevelDesc(anyInt()))
                    .thenReturn(Optional.empty());

            // Act
            MyRankingResponse result = getMyRankingService.execute();

            // Assert
            assertNotNull(result);
            assertEquals("Unknown", result.levelName());
            assertEquals("Unknown", result.levelTitle());
        }
    }

    @Test
    @DisplayName("Deve calcular percentil corretamente para o jogador no topo")
    void shouldCalculatePercentileCorrectlyForTopPlayer() {
        // Arrange
        Long userId = 1L;
        RankingInfo rankingInfo = new RankingInfo(1L, 100L); // 1º lugar de 100

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(playerCharacterRepository.findByUserId(userId)).thenReturn(Optional.of(playerCharacter));
            when(playerCharacterRepository.findPlayerPosition(playerCharacter.getId())).thenReturn(rankingInfo);
            when(levelRepository.findTopByOrderLevelLessThanEqualOrderByOrderLevelDesc(anyInt()))
                    .thenReturn(Optional.of(level));

            // Act
            MyRankingResponse result = getMyRankingService.execute();

            // Assert
            assertNotNull(result);
            assertEquals(100.0, result.percentile()); // Top 100%
        }
    }

    @Test
    @DisplayName("Deve calcular percentil corretamente para o jogador na última posição")
    void shouldCalculatePercentileCorrectlyForLastPlayer() {
        // Arrange
        Long userId = 1L;
        RankingInfo rankingInfo = new RankingInfo(100L, 100L); // Último lugar

        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(playerCharacterRepository.findByUserId(userId)).thenReturn(Optional.of(playerCharacter));
            when(playerCharacterRepository.findPlayerPosition(playerCharacter.getId())).thenReturn(rankingInfo);
            when(levelRepository.findTopByOrderLevelLessThanEqualOrderByOrderLevelDesc(anyInt()))
                    .thenReturn(Optional.of(level));

            // Act
            MyRankingResponse result = getMyRankingService.execute();

            // Assert
            assertNotNull(result);
            assertEquals(1.0, result.percentile()); // Top 1%
        }
    }
}
