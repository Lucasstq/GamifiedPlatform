package dev.gamified.GamifiedPlatform.services.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Serviço para implementar rate limiting usando Redis.
 * Protege endpoints contra abuso e ataques de força bruta.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RateLimitService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    /**
     * Verifica se o usuário/IP pode fazer uma requisição.
     *
     * @param key Identificador único (username, email, IP, etc)
     * @param maxAttempts Número máximo de tentativas permitidas
     * @param windowDuration Janela de tempo para o limite
     * @return true se a requisição é permitida, false se excedeu o limite
     */
    public boolean isAllowed(String key, int maxAttempts, Duration windowDuration) {
        String redisKey = RATE_LIMIT_PREFIX + key;

        // Incrementa o contador
        Long currentAttempts = redisTemplate.opsForValue().increment(redisKey);

        if (currentAttempts == null) {
            log.error("Failed to increment rate limit counter for key: {}", key);
            return true; // Em caso de erro, permite a requisição
        }

        // Se é a primeira tentativa, define o TTL
        if (currentAttempts == 1) {
            redisTemplate.expire(redisKey, windowDuration);
        }

        boolean allowed = currentAttempts <= maxAttempts;

        if (!allowed) {
            log.warn("Rate limit exceeded for key: {} ({}/{})", key, currentAttempts, maxAttempts);
        }

        return allowed;
    }

    /**
     * Reseta o contador de rate limit para uma chave específica.
     * Útil após login bem-sucedido, por exemplo.
     */
    public void reset(String key) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        redisTemplate.delete(redisKey);
        log.debug("Rate limit reset for key: {}", key);
    }

    /**
     * Obtém o número de tentativas restantes.
     */
    public int getRemainingAttempts(String key, int maxAttempts) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        Object attempts = redisTemplate.opsForValue().get(redisKey);

        if (attempts instanceof Number) {
            int currentAttempts = ((Number) attempts).intValue();
            return Math.max(0, maxAttempts - currentAttempts);
        }

        return maxAttempts;
    }

    /**
     * Obtém o tempo restante até o reset do rate limit em segundos.
     */
    public long getResetTime(String key) {
        String redisKey = RATE_LIMIT_PREFIX + key;
        Long ttl = redisTemplate.getExpire(redisKey, TimeUnit.SECONDS);
        return ttl != null && ttl > 0 ? ttl : 0;
    }

    /**
     * Rate limiting para tentativas de login.
     * Limite: 5 tentativas a cada 15 minutos
     */
    public boolean isLoginAllowed(String identifier) {
        return isAllowed("login:" + identifier, 5, Duration.ofMinutes(15));
    }

    /**
     * Rate limiting para envio de emails de verificação.
     * Limite: 3 emails a cada 1 hora
     */
    public boolean isEmailVerificationAllowed(String email) {
        return isAllowed("email_verification:" + email, 3, Duration.ofHours(1));
    }

    /**
     * Rate limiting para submissão de missões.
     * Limite: 10 submissões a cada 5 minutos
     */
    public boolean isMissionSubmissionAllowed(Long userId) {
        return isAllowed("mission_submission:" + userId, 10, Duration.ofMinutes(5));
    }

    /**
     * Rate limiting genérico por IP.
     * Limite: 100 requisições por minuto
     */
    public boolean isIpAllowed(String ipAddress) {
        return isAllowed("ip:" + ipAddress, 100, Duration.ofMinutes(1));
    }

    /**
     * Rate limiting para submissão de boss.
     * Limite: 3 tentativas a cada 1 hora
     */
    public boolean isBossSubmissionAllowed(Long userId) {
        return isAllowed("boss_submission:" + userId, 3, Duration.ofHours(1));
    }
}

