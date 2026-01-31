package dev.gamified.GamifiedPlatform.services.ranking;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.RankingResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRankingByLevelServiceTest {

    @Mock private PlayerCharacterRepository playerCharacterRepository;
    @Mock private LevelRepository levelRepository;
    @Mock private RedisTemplate<String, Object> redisTemplate;
    @Mock private RefreshRankingCacheService refreshRankingCacheService;
    @Mock private ZSetOperations<String, Object> zSetOperations;

    private GetRankingByLevelService service;

    @BeforeEach
    void setUp() {
        service = new GetRankingByLevelService(
                playerCharacterRepository,
                levelRepository,
                redisTemplate,
                refreshRankingCacheService
        );
    }

    @Test
    @DisplayName("Deve lançar ResourceNotFoundException quando level não existir")
    void execute_shouldThrowResourceNotFound_whenLevelNotFound() {
        Long levelId = 99L;
        Pageable pageable = PageRequest.of(0, 10);

        when(levelRepository.findById(levelId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.execute(levelId, pageable));

        verify(levelRepository).findById(levelId);
        verifyNoInteractions(redisTemplate, refreshRankingCacheService, playerCharacterRepository);
    }

    @Test
    @DisplayName("Deve chamar refresh quando cache do ranking por level estiver vazio e retornar vazio se não houver IDs")
    void execute_shouldRefreshCache_whenCacheEmpty_andReturnEmptyWhenNoIds() {
        Long levelId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Levels level = new Levels();
        level.setId(levelId);
        level.setOrderLevel(3);

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        // cache vazio -> refresh -> continua vazio
        when(zSetOperations.size("ranking_by_level:3")).thenReturn(0L, 0L);
        when(zSetOperations.reverseRange("ranking_by_level:3", 0, 9)).thenReturn(Collections.emptySet());

        Page<RankingResponse> page = service.execute(levelId, pageable);

        assertNotNull(page);
        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());

        verify(refreshRankingCacheService).execute();
        verify(zSetOperations, times(2)).size("ranking_by_level:3");
        verify(zSetOperations).reverseRange("ranking_by_level:3", 0, 9);

        verifyNoInteractions(playerCharacterRepository);
    }

    @Test
    @DisplayName("Deve retornar página vazia quando reverseRange não retornar IDs")
    void execute_shouldReturnEmptyPage_whenReverseRangeReturnsNull() {
        Long levelId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Levels level = new Levels();
        level.setId(levelId);
        level.setOrderLevel(3);

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(level));
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        when(zSetOperations.size("ranking_by_level:3")).thenReturn(50L);
        when(zSetOperations.reverseRange("ranking_by_level:3", 0, 9)).thenReturn(null);

        Page<RankingResponse> page = service.execute(levelId, pageable);

        assertTrue(page.getContent().isEmpty());
        assertEquals(50L, page.getTotalElements());

        verify(refreshRankingCacheService, never()).execute();
        verifyNoInteractions(playerCharacterRepository);
    }

    @Test
    @DisplayName("Deve montar ranking do level e marcar isMe para usuário autenticado")
    void execute_shouldBuildRankingAndMarkIsMe() {
        Long levelId = 1L;
        Pageable pageable = PageRequest.of(0, 3);

        Levels requestedLevel = new Levels();
        requestedLevel.setId(levelId);
        requestedLevel.setOrderLevel(3);

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(requestedLevel));
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        when(zSetOperations.size("ranking_by_level:3")).thenReturn(3L);

        Set<Object> ids = new LinkedHashSet<>(List.of(30L, 10L, 20L));
        when(zSetOperations.reverseRange("ranking_by_level:3", 0, 2)).thenReturn(ids);

        PlayerCharacter c10 = character(10L, "Char10", 3, 150, user(1L, "u1"));
        PlayerCharacter c20 = character(20L, "Char20", 3, 999, user(2L, "u2"));
        PlayerCharacter c30 = character(30L, "Char30", 3, 10,  user(3L, "u3"));

        when(playerCharacterRepository.findAllByIdInWithUser(anyList()))
                .thenReturn(List.of(c10, c20, c30));

        Levels levelInfo = new Levels();
        levelInfo.setOrderLevel(3);
        levelInfo.setName("Level 3");
        levelInfo.setTitle("Adept");

        when(levelRepository.findAllByOrderLevelIn(Set.of(3)))
                .thenReturn(List.of(levelInfo));

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(3L));

            Page<RankingResponse> page = service.execute(levelId, pageable);

            assertEquals(3, page.getContent().size());
            assertEquals(3L, page.getTotalElements());

            RankingResponse r1 = page.getContent().get(0);
            RankingResponse r2 = page.getContent().get(1);
            RankingResponse r3 = page.getContent().get(2);

            // ordem conforme Redis: 30,10,20
            assertEquals(3L, r1.userId()); // char30
            assertEquals(1L, r2.userId()); // char10
            assertEquals(2L, r3.userId()); // char20

            assertEquals(1, r1.position());
            assertEquals(2, r2.position());
            assertEquals(3, r3.position());

            assertTrue(r1.isMe());
            assertFalse(r2.isMe());
            assertFalse(r3.isMe());

            assertEquals("Level 3", r1.levelName());
            assertEquals("Adept", r1.levelTitle());
        }

        // Captura a lista usada no batch pra garantir ordem
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Long>> idCaptor = ArgumentCaptor.forClass((Class) List.class);

        verify(playerCharacterRepository).findAllByIdInWithUser(idCaptor.capture());
        assertEquals(List.of(30L, 10L, 20L), idCaptor.getValue());

        verify(levelRepository).findAllByOrderLevelIn(Set.of(3));
        verify(refreshRankingCacheService, never()).execute();
    }



    @Test
    @DisplayName("Deve usar Unknown quando não encontrar informações do level do personagem")
    void execute_shouldUseUnknown_whenLevelInfoNotFound() {
        Long levelId = 1L;
        Pageable pageable = PageRequest.of(0, 1);

        Levels requestedLevel = new Levels();
        requestedLevel.setId(levelId);
        requestedLevel.setOrderLevel(2);

        when(levelRepository.findById(levelId)).thenReturn(Optional.of(requestedLevel));
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        when(zSetOperations.size("ranking_by_level:2")).thenReturn(1L);
        when(zSetOperations.reverseRange("ranking_by_level:2", 0, 0))
                .thenReturn(new LinkedHashSet<>(List.of(10L)));

        PlayerCharacter c10 = character(10L, "Char10", 2, 10, user(7L, "u7"));

        when(playerCharacterRepository.findAllByIdInWithUser(List.of(10L)))
                .thenReturn(List.of(c10));

        when(levelRepository.findAllByOrderLevelIn(Set.of(2)))
                .thenReturn(List.of());

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.empty());

            Page<RankingResponse> page = service.execute(levelId, pageable);

            assertEquals(1, page.getContent().size());
            RankingResponse r = page.getContent().get(0);

            assertEquals("Unknown", r.levelName());
            assertEquals("Unknown", r.levelTitle());
            assertFalse(r.isMe());
        }

        verify(playerCharacterRepository).findAllByIdInWithUser(List.of(10L));
        verify(levelRepository).findAllByOrderLevelIn(Set.of(2));
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
}

