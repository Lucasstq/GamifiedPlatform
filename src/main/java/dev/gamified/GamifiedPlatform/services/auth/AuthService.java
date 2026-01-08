package dev.gamified.GamifiedPlatform.services.auth;

import dev.gamified.GamifiedPlatform.domain.RefreshToken;
import dev.gamified.GamifiedPlatform.domain.Scopes;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.auth.LoginRequest;
import dev.gamified.GamifiedPlatform.dtos.response.LoginResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.security.RateLimitService;
import dev.gamified.GamifiedPlatform.services.security.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final RateLimitService rateLimitService;
    private final RefreshTokenService refreshTokenService;
    private final SecurityAuditService auditService;

    @Value("${jwt.access-token.expiration:900}") // 15 minutos
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration:604800}") // 7 dias
    private Long refreshTokenExpiration;

    public LoginResponse authenticate(LoginRequest request, String ipAddress, String userAgent) {
        // Verifica rate limiting antes de processar login
        if (!rateLimitService.isLoginAllowed(request.username())) {
            long resetTime = rateLimitService.getResetTime("login:" + request.username());

            // Log de tentativa bloqueada por rate limit
            auditService.logSuspiciousActivity(null, request.username(), ipAddress,
                "Rate limit exceeded - Too many login attempts");

            throw new BusinessException(
                "Too many login attempts. Please try again in " + (resetTime / 60) + " minutes"
            );
        }

        User user = userRepository.findUserByUsername(request.username())
                .orElseThrow(() -> {
                    // Log de falha de login (usuário não encontrado)
                    auditService.logLoginFailure(request.username(), ipAddress, userAgent, "User not found");
                    return new BusinessException("Invalid username or password");
                });

        try {
            isPasswordCorrect(request.password(), user.getPassword());
            isEmailVerified(user);
            isUserActive(user);
        } catch (BusinessException e) {
            // Log de falha de login (senha incorreta, email não verificado, etc)
            auditService.logLoginFailure(user.getUsername(), ipAddress, userAgent, e.getMessage());
            throw e;
        }

        // Login bem-sucedido - reseta o contador de tentativas
        rateLimitService.reset("login:" + request.username());

        // Gerar access token (curta duração)
        String accessToken = generateAccessToken(user);

        // Gerar refresh token (longa duração)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, ipAddress, userAgent);

        // Log de sucesso
        auditService.logLoginSuccess(user.getId(), user.getUsername(), ipAddress, userAgent);
        log.info("User logged in successfully: {} from IP: {}", user.getUsername(), ipAddress);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .refreshExpiresIn(refreshTokenExpiration)
                .build();
    }

    /**
     * Renova o access token usando um refresh token válido.
     */
    public LoginResponse refreshAccessToken(String refreshTokenValue, String ipAddress) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(refreshTokenValue);
        User user = refreshToken.getUser();

        // Gerar novo access token
        String accessToken = generateAccessToken(user);

        // Log de refresh
        auditService.logTokenRefresh(user.getId(), user.getUsername(), ipAddress);
        log.info("Access token refreshed for user: {}", user.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue) // Mesmo refresh token
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration)
                .refreshExpiresIn(refreshTokenExpiration)
                .build();
    }

    /**
     * Logout: revoga o refresh token.
     */
    public void logout(String refreshTokenValue, String ipAddress) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(refreshTokenValue);
        User user = refreshToken.getUser();

        refreshTokenService.revokeRefreshToken(refreshTokenValue);

        // Log de logout
        auditService.logLogout(user.getId(), user.getUsername(), ipAddress);
        log.info("User logged out successfully: {}", user.getUsername());
    }

    /**
     * Logout de todos os dispositivos: revoga todos os refresh tokens do usuário.
     */
    public void logoutAllDevices(Long userId, String ipAddress) {
        refreshTokenService.revokeAllUserTokens(userId);
        // Log seria adicionado aqui se tivéssemos o username
        log.info("User logged out from all devices: userId={}", userId);
    }

    private void isPasswordCorrect(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BusinessException("Invalid username or password");
        }
    }

    private void isUserActive(User user) {
        if (!user.getActive()) {
            throw new BusinessException("User account is inactive");
        }
    }

    private void isEmailVerified(User user) {
        if (!user.getEmailVerified()) {
            throw new BusinessException("Email address is not verified");
        }
    }

    private String generateAccessToken(User user) {
        List<String> scopes = user.getScopes().stream()
                .map(Scopes::getName)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("gamified-platform")
                .subject(user.getUsername())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(accessTokenExpiration))
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("scope", scopes)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}

