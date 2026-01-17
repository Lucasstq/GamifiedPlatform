package dev.gamified.GamifiedPlatform.services.ranking;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.RankingResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetGlobalRankingServiceTest {

    @Mock
    private PlayerCharacterRepository playerCharacterRepository;
    @Mock
    private LevelRepository levelRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private RefreshRankingCacheService refreshRankingCacheService;
    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    private GetGlobalRankingService service;

    @BeforeEach
    void setUp() {
        service = new GetGlobalRankingService(
                playerCharacterRepository,
                levelRepository,
                redisTemplate,
                refreshRankingCacheService
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando page size exceder 1000")
    void execute_shouldThrowBusinessException_whenPageSizeTooLarge() {
        Pageable pageable = PageRequest.of(0, 1001);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.execute(pageable));

        assertEquals("Page size cannot exceed 1000", ex.getMessage());

        verifyNoInteractions(redisTemplate, refreshRankingCacheService, playerCharacterRepository, levelRepository);
    }

    @Test
    @DisplayName("Deve chamar refresh quando cache estiver vazio e retornar página vazia se ainda não houver IDs")
    void execute_shouldRefreshCache_whenCacheIsEmpty_andReturnEmptyWhenNoIds() {
        Pageable pageable = PageRequest.of(0, 10);

        // 1ª chamada: cache vazio -> refresh
        when(zSetOperations.size("global_ranking")).thenReturn(0L, 0L);
        when(zSetOperations.reverseRange("global_ranking", 0, 9)).thenReturn(Collections.emptySet());

        Page<RankingResponse> page = service.execute(pageable);

        assertNotNull(page);
        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());

        verify(refreshRankingCacheService).execute();
        verify(zSetOperations, times(2)).size("global_ranking");
        verify(zSetOperations).reverseRange("global_ranking", 0, 9);
        verifyNoInteractions(playerCharacterRepository, levelRepository);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando reverseRange não retornar IDs")
    void execute_shouldReturnEmptyPage_whenReverseRangeReturnsNullOrEmpty() {
        Pageable pageable = PageRequest.of(0, 10);

        when(zSetOperations.size("global_ranking")).thenReturn(100L);
        when(zSetOperations.reverseRange("global_ranking", 0, 9)).thenReturn(null);

        Page<RankingResponse> page = service.execute(pageable);

        assertTrue(page.getContent().isEmpty());
        assertEquals(100L, page.getTotalElements());

        verify(refreshRankingCacheService, never()).execute();
        verifyNoInteractions(playerCharacterRepository, levelRepository);
    }

    @Test
    @DisplayName("Deve montar ranking mantendo a ordem do ZSET e marcar isMe para o usuário autenticado")
    void execute_shouldBuildRankingInCorrectOrder_andMarkIsMe() {
        Pageable pageable = PageRequest.of(0, 3);

        // cache com 50 players
        when(zSetOperations.size("global_ranking")).thenReturn(50L);

        // ordem do ranking (desc) vinda do Redis
        Set<Object> redisIds = new LinkedHashSet<>(List.of(30L, 10L, 20L));
        when(zSetOperations.reverseRange("global_ranking", 0, 2)).thenReturn(redisIds);

        // personagens (repo pode retornar fora de ordem)
        PlayerCharacter c10 = character(10L, "Char10", 3, 150, user(1L, "u1"));
        PlayerCharacter c20 = character(20L, "Char20", 2, 999, user(2L, "u2"));
        PlayerCharacter c30 = character(30L, "Char30", 5, 10, user(3L, "u3")); // será "isMe"

        when(playerCharacterRepository.findAllByIdInWithUser(List.of(30L, 10L, 20L)))
                .thenReturn(List.of(c10, c20, c30)); // fora de ordem de propósito

        // levels
        Levels level2 = level(2, "L2", "Title2");
        Levels level3 = level(3, "L3", "Title3");
        Levels level5 = level(5, "L5", "Title5");

        when(levelRepository.findAllByOrderLevelIn(new HashSet<>(List.of(3, 2, 5))))
                .thenReturn(List.of(level2, level3, level5));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(3L));

            Page<RankingResponse> page = service.execute(pageable);

            assertEquals(3, page.getContent().size());
            assertEquals(50L, page.getTotalElements());

            // garante ordem do ranking conforme Redis: 30, 10, 20
            RankingResponse r1 = page.getContent().get(0);
            RankingResponse r2 = page.getContent().get(1);
            RankingResponse r3 = page.getContent().get(2);

            assertEquals(1, r1.position());
            assertEquals(3L, r1.userId());
            assertEquals("u3", r1.username());
            assertEquals("Char30", r1.characterName());
            assertEquals(5, r1.level());
            assertEquals(10, r1.xp());
            assertEquals("L5", r1.levelName());
            assertEquals("Title5", r1.levelTitle());
            assertTrue(r1.isMe());

            assertEquals(2, r2.position());
            assertEquals(1L, r2.userId());
            assertFalse(r2.isMe());

            assertEquals(3, r3.position());
            assertEquals(2L, r3.userId());
            assertFalse(r3.isMe());
        }

        verify(refreshRankingCacheService, never()).execute();
        verify(playerCharacterRepository).findAllByIdInWithUser(List.of(30L, 10L, 20L));
        verify(levelRepository).findAllByOrderLevelIn(anySet());
    }

    @Test
    @DisplayName("Deve preencher levelName e levelTitle como Unknown quando level não existir")
    void execute_shouldUseUnknown_whenLevelNotFoundInDb() {
        Pageable pageable = PageRequest.of(0, 1);

        when(zSetOperations.size("global_ranking")).thenReturn(1L);
        when(zSetOperations.reverseRange("global_ranking", 0, 0))
                .thenReturn(new LinkedHashSet<>(List.of(10L)));

        PlayerCharacter c10 = character(10L, "Char10", 99, 10, user(7L, "u7"));
        when(playerCharacterRepository.findAllByIdInWithUser(List.of(10L)))
                .thenReturn(List.of(c10));

        // não retorna o level 99
        when(levelRepository.findAllByOrderLevelIn(Set.of(99)))
                .thenReturn(List.of());

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.empty());

            Page<RankingResponse> page = service.execute(pageable);

            RankingResponse r = page.getContent().get(0);
            assertEquals("Unknown", r.levelName());
            assertEquals("Unknown", r.levelTitle());
            assertFalse(r.isMe());
        }
    }

    private User user(Long id, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        return u;
    }

    private PlayerCharacter character(Long id, String name, int level, int xp, User user) {
        PlayerCharacter c = new PlayerCharacter();
        c.setId(id);
        c.setName(name);
        c.setLevel(level);
        c.setXp(xp);
        c.setUser(user);
        return c;
    }

    private Levels level(int orderLevel, String name, String title) {
        Levels l = new Levels();
        l.setOrderLevel(orderLevel);
        l.setName(name);
        l.setTitle(title);
        return l;
    }
}

