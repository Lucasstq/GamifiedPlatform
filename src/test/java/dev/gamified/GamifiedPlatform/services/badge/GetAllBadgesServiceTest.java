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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
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
    private Pageable pageable;

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

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should return all badges successfully")
    void shouldReturnAllBadgesSuccessfully() {
        // Arrange
        List<Badge> badges = Arrays.asList(badge1, badge2);
        Page<Badge> badgePage = new PageImpl<>(badges, pageable, badges.size());
        when(badgeRepository.findAll(pageable)).thenReturn(badgePage);

        // Act
        Page<BadgeResponse> result = getAllBadgesService.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(2, result.getContent().size());
        assertEquals("Vencedor da Sintaxe", result.getContent().get(0).name());
        assertEquals("Mestre dos Arrays", result.getContent().get(1).name());
        verify(badgeRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return empty page when no badges exist")
    void shouldReturnEmptyPageWhenNoBadgesExist() {
        // Arrange
        Page<Badge> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(badgeRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        Page<BadgeResponse> result = getAllBadgesService.execute(pageable);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(0, result.getTotalElements());
        verify(badgeRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should return badges with correct level information")
    void shouldReturnBadgesWithCorrectLevelInformation() {
        // Arrange
        List<Badge> badges = Collections.singletonList(badge1);
        Page<Badge> badgePage = new PageImpl<>(badges, pageable, badges.size());
        when(badgeRepository.findAll(pageable)).thenReturn(badgePage);

        // Act
        Page<BadgeResponse> result = getAllBadgesService.execute(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        BadgeResponse response = result.getContent().get(0);
        assertEquals(1L, (long) response.levelId());
        assertEquals("Aprendiz do Código", response.levelName());
        assertEquals(1, response.levelOrder().intValue());
    }

    @Test
    @DisplayName("Should return correct page information")
    void shouldReturnCorrectPageInformation() {
        // Arrange
        Pageable customPageable = PageRequest.of(1, 1);
        List<Badge> badges = Collections.singletonList(badge2);
        Page<Badge> badgePage = new PageImpl<>(badges, customPageable, 2);
        when(badgeRepository.findAll(customPageable)).thenReturn(badgePage);

        // Act
        Page<BadgeResponse> result = getAllBadgesService.execute(customPageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getNumber());
        assertEquals(1, result.getSize());
        assertEquals(2, result.getTotalPages());
        verify(badgeRepository, times(1)).findAll(customPageable);
    }
}