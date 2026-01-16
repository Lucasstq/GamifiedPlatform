package dev.gamified.GamifiedPlatform.services.ranking;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collection;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshRankingCacheService Tests")
class RefreshRankingCacheServiceTest {
    @Mock
    private PlayerCharacterRepository playerCharacterRepository;
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private ZSetOperations<String, Object> zSetOps;
    @InjectMocks
    private RefreshRankingCacheService refreshRankingCacheService;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForZSet()).thenReturn(zSetOps);
    }

    @Test
    @DisplayName("Atualização atômica do ranking não lança exceção")
    void testAtualizacaoAtomica() {
        PlayerCharacter pc = new PlayerCharacter();
        pc.setId(1L);
        pc.setLevel(1);
        pc.setXp(100);
        List<PlayerCharacter> pcs = List.of(pc);
        when(playerCharacterRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenReturn(pcs);
        when(zSetOps.add(any(), any(), anyDouble())).thenReturn(true);
        when(zSetOps.size(contains("global_ranking:temp:"))).thenReturn(1L);
        when(zSetOps.size(contains("ranking_by_level:1:temp:"))).thenReturn(1L);
        for (int i = 2; i <= 10; i++) {
            when(zSetOps.size(contains("ranking_by_level:" + i + ":temp:"))).thenReturn(0L);
        }
        doNothing().when(redisTemplate).rename(contains("global_ranking:temp:"), eq("global_ranking"));
        when(redisTemplate.delete(any(String.class))).thenReturn(true);
        when(redisTemplate.delete(any(Collection.class))).thenReturn(1L);
        refreshRankingCacheService.execute();
        verify(zSetOps, atLeastOnce()).add(any(), any(), anyDouble());
        verify(redisTemplate, atLeastOnce()).delete(any(String.class));
        verify(redisTemplate).rename(contains("global_ranking:temp:"), eq("global_ranking"));
    }

    @Test
    @DisplayName("Erro no Redis é tratado e logado")
    void testErroRedis() {
        when(playerCharacterRepository.findAll(any(org.springframework.data.domain.Sort.class))).thenThrow(new RuntimeException("Redis error"));
        refreshRankingCacheService.execute();
        // Não lança exceção
    }

    @AfterEach
    void tearDown() throws Exception {
        if (autoCloseable != null) autoCloseable.close();
    }
}
