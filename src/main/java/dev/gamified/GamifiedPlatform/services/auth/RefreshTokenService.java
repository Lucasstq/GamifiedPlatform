package dev.gamified.GamifiedPlatform.services.auth;

import dev.gamified.GamifiedPlatform.domain.RefreshToken;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Serviço para gerenciar refresh tokens.
 * Permite renovação de access tokens sem novo login.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token.expiration:604800}") // 7 dias em segundos
    private Long refreshTokenExpiration;

    /*
     * Cria um novo refresh token para o usuário.
     */
    @Transactional
    public RefreshToken createRefreshToken(User user, String ipAddress, String userAgent) {
        String token = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusSeconds(refreshTokenExpiration))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .revoked(false)
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);
        log.info("Refresh token created for user: {} (expires in {} days)",
                 user.getUsername(), refreshTokenExpiration / 86400);

        return refreshToken;
    }

    /**
     * Valida e retorna um refresh token.
     */
    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            log.warn("Attempt to use revoked refresh token for user: {}",
                     refreshToken.getUser().getUsername());
            throw new BusinessException("Refresh token has been revoked");
        }

        if (refreshToken.isExpired()) {
            log.warn("Attempt to use expired refresh token for user: {}",
                     refreshToken.getUser().getUsername());
            throw new BusinessException("Refresh token has expired. Please login again");
        }

        return refreshToken;
    }

    @Transactional
    public void saveRevokedToken(RefreshToken refreshToken) {
        refreshTokenRepository.save(refreshToken);
    }

    /**
     * Revoga um refresh token específico.
     */
    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Invalid refresh token"));

        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        log.info("Refresh token revoked for user: {}", refreshToken.getUser().getUsername());
    }

    /**
     * Revoga todos os refresh tokens de um usuário (logout em todos dispositivos).
     */
    @Transactional
    public void revokeAllUserTokens(Long userId) {
        int revoked = refreshTokenRepository.revokeAllByUserId(userId);
        log.info("Revoked {} refresh tokens for user ID: {}", revoked, userId);
    }

    /*
     * Remove tokens expirados do banco (executar periodicamente).
     */
    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = refreshTokenRepository.deleteExpiredTokens();
        if (deleted > 0) {
            log.info("Cleaned up {} expired refresh tokens", deleted);
        }
    }
}

