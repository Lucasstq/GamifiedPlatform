package dev.gamified.GamifiedPlatform.services.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * Serviço para gerenciar tokens de verificação de email no Redis.
 * Oferece melhor performance e TTL automático.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationTokenService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TOKEN_PREFIX = "email:verification:token:";
    private static final String USER_PREFIX = "email:verification:user:";
    private static final Duration TOKEN_EXPIRATION = Duration.ofHours(24);

    /**
     * Armazena o token de verificação no Redis com TTL de 24 horas.
     * Cria mapeamento bidirecional: token -> userId e userId -> token
     */
    public void saveToken(Long userId, String token) {
        String tokenKey = TOKEN_PREFIX + token;
        String userKey = USER_PREFIX + userId;

        // Remove token anterior do usuário, se existir
        removeOldToken(userId);

        // Armazena token -> userId
        redisTemplate.opsForValue().set(tokenKey, userId, TOKEN_EXPIRATION);

        // Armazena userId -> token (para facilitar remoção futura)
        redisTemplate.opsForValue().set(userKey, token, TOKEN_EXPIRATION);

        log.debug("Verification token saved in Redis for user {}", userId);
    }

    /**
     * Busca o ID do usuário associado ao token.
     */
    public Optional<Long> getUserIdByToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        Object userId = redisTemplate.opsForValue().get(tokenKey);

        if (userId instanceof Number) {
            return Optional.of(((Number) userId).longValue());
        }

        return Optional.empty();
    }

    /**
     * Verifica se o token existe e está válido.
     */
    public boolean isValidToken(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(tokenKey));
    }

    /**
     * Remove o token após verificação bem-sucedida.
     */
    public void removeToken(String token, Long userId) {
        String tokenKey = TOKEN_PREFIX + token;
        String userKey = USER_PREFIX + userId;

        redisTemplate.delete(tokenKey);
        redisTemplate.delete(userKey);

        log.debug("Verification token removed in Redis for user {}", userId);
    }

    /**
     * Remove o token antigo do usuário antes de criar um novo.
     */
    private void removeOldToken(Long userId) {
        String userKey = USER_PREFIX + userId;
        Object oldToken = redisTemplate.opsForValue().get(userKey);

        if (oldToken != null) {
            String oldTokenKey = TOKEN_PREFIX + oldToken;
            redisTemplate.delete(oldTokenKey);
            log.debug("Remove old token for user {}", userId);
        }
    }

    /**
     * Obtém o tempo restante de validade do token em segundos.
     */
    public Optional<Long> getTokenTTL(String token) {
        String tokenKey = TOKEN_PREFIX + token;
        Long ttl = redisTemplate.getExpire(tokenKey);

        if (ttl != null && ttl > 0) {
            return Optional.of(ttl);
        }

        return Optional.empty();
    }
}

