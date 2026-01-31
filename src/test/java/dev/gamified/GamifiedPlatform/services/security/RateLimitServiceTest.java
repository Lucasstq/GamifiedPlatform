package dev.gamified.GamifiedPlatform.services.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitService Tests")
class RateLimitServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Deve permitir requisição quando dentro do limite")
    void shouldAllowRequestWhenWithinLimit() {
        when(valueOperations.increment("rate_limit:test_key")).thenReturn(1L);
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);

        boolean allowed = rateLimitService.isAllowed("test_key", 5, Duration.ofMinutes(15));

        assertTrue(allowed);
        verify(valueOperations).increment("rate_limit:test_key");
        verify(redisTemplate).expire(eq("rate_limit:test_key"), any(Duration.class));
    }

    @Test
    @DisplayName("Deve bloquear requisição quando exceder limite")
    void shouldBlockRequestWhenLimitExceeded() {
        when(valueOperations.increment("rate_limit:test_key")).thenReturn(6L);

        boolean allowed = rateLimitService.isAllowed("test_key", 5, Duration.ofMinutes(15));

        assertFalse(allowed);
        verify(valueOperations).increment("rate_limit:test_key");
    }

    @Test
    @DisplayName("Deve permitir quando contador retorna null (erro)")
    void shouldAllowWhenCounterReturnsNull() {
        when(valueOperations.increment("rate_limit:test_key")).thenReturn(null);

        boolean allowed = rateLimitService.isAllowed("test_key", 5, Duration.ofMinutes(15));

        assertTrue(allowed);
    }

    @Test
    @DisplayName("Deve definir TTL apenas na primeira tentativa")
    void shouldSetTtlOnlyOnFirstAttempt() {
        when(valueOperations.increment("rate_limit:test_key")).thenReturn(3L);

        rateLimitService.isAllowed("test_key", 5, Duration.ofMinutes(15));

        verify(redisTemplate, never()).expire(anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("Deve resetar o rate limit")
    void shouldResetRateLimit() {
        when(redisTemplate.delete("rate_limit:test_key")).thenReturn(true);

        rateLimitService.reset("test_key");

        verify(redisTemplate).delete("rate_limit:test_key");
    }

    @Test
    @DisplayName("Deve retornar tentativas restantes")
    void shouldReturnRemainingAttempts() {
        when(valueOperations.get("rate_limit:test_key")).thenReturn(3);

        int remaining = rateLimitService.getRemainingAttempts("test_key", 5);

        assertEquals(2, remaining);
    }

    @Test
    @DisplayName("Deve retornar max quando não há tentativas")
    void shouldReturnMaxWhenNoAttempts() {
        when(valueOperations.get("rate_limit:test_key")).thenReturn(null);

        int remaining = rateLimitService.getRemainingAttempts("test_key", 5);

        assertEquals(5, remaining);
    }

    @Test
    @DisplayName("Deve retornar zero quando excedeu limite")
    void shouldReturnZeroWhenExceededLimit() {
        when(valueOperations.get("rate_limit:test_key")).thenReturn(10);

        int remaining = rateLimitService.getRemainingAttempts("test_key", 5);

        assertEquals(0, remaining);
    }

    @Test
    @DisplayName("Deve retornar tempo de reset")
    void shouldReturnResetTime() {
        when(redisTemplate.getExpire("rate_limit:test_key", TimeUnit.SECONDS)).thenReturn(120L);

        long resetTime = rateLimitService.getResetTime("test_key");

        assertEquals(120L, resetTime);
    }

    @Test
    @DisplayName("Deve retornar zero quando TTL é null ou negativo")
    void shouldReturnZeroWhenTtlIsNullOrNegative() {
        when(redisTemplate.getExpire("rate_limit:test_key", TimeUnit.SECONDS)).thenReturn(-1L);

        long resetTime = rateLimitService.getResetTime("test_key");

        assertEquals(0, resetTime);
    }

    @Test
    @DisplayName("isLoginAllowed deve usar limites corretos")
    void shouldUseCorrectLimitsForLogin() {
        when(valueOperations.increment("rate_limit:login:user123")).thenReturn(1L);
        when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(true);

        boolean allowed = rateLimitService.isLoginAllowed("user123");

        assertTrue(allowed);
        verify(valueOperations).increment("rate_limit:login:user123");
    }

    @Test
    @DisplayName("isLoginAllowed deve bloquear após 5 tentativas")
    void shouldBlockLoginAfterFiveAttempts() {
        when(valueOperations.increment("rate_limit:login:user123")).thenReturn(6L);

        boolean allowed = rateLimitService.isLoginAllowed("user123");

        assertFalse(allowed);
    }
}

