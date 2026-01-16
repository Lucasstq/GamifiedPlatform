package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.constants.BusinessConstants;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.enums.DifficultyLevel;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CalculateLevelProgressService Tests")
class CalculateLevelProgressServiceTest {

    @Mock
    private GetNextLevelService getNextLevel;
    @Mock
    private LevelRepository levelRepository;
    @InjectMocks
    private CalculateLevelProgressService calculateLevelProgressService;

    private Levels currentLevel;
    private LevelResponse nextLevel;

    @BeforeEach
    void setUp() {
        currentLevel = Levels.builder().id(1L).orderLevel(1).xpRequired(100).build();
        nextLevel = new LevelResponse(
            2L, // id
            2, // orderLevel
            "Intermediário", // name
            "Título", // title
            "desc", // description
            200, // xpRequired
            "img", // iconUrl
            DifficultyLevel.MEDIUM, // difficultyLevel
            LocalDateTime.now(), // createdAt
            LocalDateTime.now() // updatedAt
        );
    }

    @Test
    @DisplayName("Deve calcular progresso corretamente para nível intermediário")
    void shouldCalculateProgressCorrectly() {
        when(levelRepository.findByOrderLevel(1)).thenReturn(Optional.of(currentLevel));
        when(getNextLevel.execute(1)).thenReturn(nextLevel);
        double progress = calculateLevelProgressService.execute(150, 1);
        assertEquals(50.0, progress);
    }

    @Test
    @DisplayName("Deve retornar progresso máximo se não houver próximo nível")
    void shouldReturnMaxProgressIfNoNextLevel() {
        when(levelRepository.findByOrderLevel(1)).thenReturn(Optional.of(currentLevel));
        when(getNextLevel.execute(1)).thenThrow(new ResourceNotFoundException("No next level"));
        double progress = calculateLevelProgressService.execute(200, 1);
        assertEquals(BusinessConstants.MAX_PROGRESS_PERCENTAGE, progress);
    }

    @Test
    @DisplayName("Deve lançar exceção se nível atual não encontrado")
    void shouldThrowIfCurrentLevelNotFound() {
        when(levelRepository.findByOrderLevel(anyInt())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> calculateLevelProgressService.execute(100, 1));
    }

    @Test
    @DisplayName("Deve limitar progresso ao mínimo e máximo")
    void shouldClampProgressToMinMax() {
        when(levelRepository.findByOrderLevel(1)).thenReturn(Optional.of(currentLevel));
        when(getNextLevel.execute(1)).thenReturn(new LevelResponse(
            2L, 2, "Intermediário", "Título", "desc", 120, "img", DifficultyLevel.MEDIUM, LocalDateTime.now(), LocalDateTime.now()
        ));
        double progress = calculateLevelProgressService.execute(90, 1); // abaixo do mínimo
        assertEquals(BusinessConstants.MIN_PROGRESS_PERCENTAGE, progress);
        progress = calculateLevelProgressService.execute(200, 1); // acima do máximo
        assertEquals(BusinessConstants.MAX_PROGRESS_PERCENTAGE, progress);
    }
}
