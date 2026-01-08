package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.domain.Badge;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.response.badges.BadgeResponse;
import dev.gamified.GamifiedPlatform.repository.BadgeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAllBadgesService Tests")
class GetAllBadgesServiceTest {

    @Mock
    private BadgeRepository badgeRepository;

    @InjectMocks
    private GetAllBadgesService getAllBadgesService;

    private Badge badge1;
    private Badge badge2;
    private Levels level1;
    private Levels level2;

    @BeforeEach
    void setUp() {
        level1 = new Levels();
        level1.setId(1L);
        level1.setName("Aprendiz do Código");
        level1.setOrderLevel(1);

        level2 = new Levels();
        level2.setId(2L);
        level2.setName("Explorador de Sintaxe");
        level2.setOrderLevel(2);

        badge1 = Badge.builder()
                .id(1L)
                .level(level1)
                .name("Vencedor da Sintaxe")
                .title("Mestre dos Fundamentos")
                .description("Concedido aos valentes que derrotaram o Syntax Sentinel")
                .rarity("EPIC")
                .iconUrl("https://img.icons8.com/color/48/code.png")
                .build();

        badge2 = Badge.builder()
                .id(2L)
                .level(level2)
                .name("Mestre dos Arrays")
                .title("Domador de Estruturas de Dados")
                .description("Outorgado aos exploradores que conquistaram o Array Archon")
                .rarity("EPIC")
                .iconUrl("https://img.icons8.com/color/48/matrix.png")
                .build();
    }

    @Test
    @DisplayName("Should return all badges successfully")
    void shouldReturnAllBadgesSuccessfully() {
        // Arrange
        List<Badge> badges = Arrays.asList(badge1, badge2);
        when(badgeRepository.findAll()).thenReturn(badges);

        // Act
        List<BadgeResponse> result = getAllBadgesService.execute();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Vencedor da Sintaxe", result.get(0).name());
        assertEquals("Mestre dos Arrays", result.get(1).name());
        verify(badgeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no badges exist")
    void shouldReturnEmptyListWhenNoBadgesExist() {
        // Arrange
        when(badgeRepository.findAll()).thenReturn(Arrays.asList());

        // Act
        List<BadgeResponse> result = getAllBadgesService.execute();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(badgeRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return badges with correct level information")
    void shouldReturnBadgesWithCorrectLevelInformation() {
        // Arrange
        List<Badge> badges = Arrays.asList(badge1);
        when(badgeRepository.findAll()).thenReturn(badges);

        // Act
        List<BadgeResponse> result = getAllBadgesService.execute();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        BadgeResponse response = result.get(0);
        assertEquals(1L, response.levelId());
        assertEquals("Aprendiz do Código", response.levelName());
        assertEquals(1, response.levelOrder());
    }
}

