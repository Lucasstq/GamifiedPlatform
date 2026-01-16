package dev.gamified.GamifiedPlatform.services.auth;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.RefreshToken;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.auth.LoginRequest;
import dev.gamified.GamifiedPlatform.dtos.response.login.LoginResponse;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.security.RateLimitService;
import dev.gamified.GamifiedPlatform.services.security.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RateLimitService rateLimitService;
    private final RefreshTokenService refreshTokenService;
    private final SecurityAuditService auditService;

    @Transactional
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
                    auditService.logLoginFailure(request.username(), ipAddress, userAgent, "User not found" +
                            request.username());
                    return new BusinessException("Invalid username or password");
                });

        // Validação extra: verificar se provider está configurado corretamente
        if (user.getProvider() == null) {
            log.error("User {} has NULL provider - database inconsistency detected", user.getUsername());
            auditService.logSuspiciousActivity(user.getId(), user.getUsername(), ipAddress,
                "Login attempted with NULL provider - database inconsistency");
            throw new BusinessException("Account configuration error. Please contact support.");
        }

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
        String accessToken = jwtTokenService.generateAccessToken(user);

        // Gerar refresh token (longa duração)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, ipAddress, userAgent);

        // Log de sucesso
        auditService.logLoginSuccess(user.getId(), user.getUsername(), ipAddress, userAgent);
        log.info("User logged in successfully: {} from IP: {}", maskUsername(user.getUsername()), maskIp(ipAddress));

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken != null ? refreshToken.getToken() : null)
                .tokenType("Bearer")
                .expiresIn(jwtTokenService.getAccessTokenExpiration())
                .refreshExpiresIn(jwtTokenService.getRefreshTokenExpiration())
                .build();
    }

    /**
     * Renova o access token usando um refresh token válido, faz rotação de refresh token.
     */
    @Transactional
    public LoginResponse refreshAccessToken(String refreshTokenValue, String ipAddress, String userAgent) {
        RefreshToken oldToken = refreshTokenService.validateRefreshToken(refreshTokenValue);
        User user = oldToken.getUser();

        oldToken.setRevoked(true);
        refreshTokenService.saveRevokedToken(oldToken);

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user,
                ipAddress, userAgent);

        //Verificação de dispositivo diferente
        boolean sameAgent = oldToken.getUserAgent().equals(userAgent);
        if(!sameAgent){
            auditService.logSuspiciousActivity(
                    user.getId(),
                    user.getUsername(),
                    ipAddress,
                    "Refresh token rotated with different user agent"
            );
        }

        // Gerar novo access token
        String accessToken = jwtTokenService.generateAccessToken(user);

        // Log de refresh
        auditService.logTokenRefresh(user.getId(), user.getUsername(), ipAddress);
        log.info("Access token refreshed for user: {}", user.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken()) // Rotação do refresh token
                .tokenType("Bearer")
                .expiresIn(jwtTokenService.getAccessTokenExpiration())
                .refreshExpiresIn(jwtTokenService.getRefreshTokenExpiration())
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
     * Obtém o userId do contexto de segurança (usuário autenticado).
     */
    public void logoutAllDevices(String ipAddress) {
        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User must be authenticated"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        refreshTokenService.revokeAllUserTokens(userId);

        // Log de logout de todos os dispositivos
        auditService.logLogoutAllDevices(user.getId(), user.getUsername(), ipAddress);
        log.info("User logged out from all devices: {} (id={})", user.getUsername(), userId);
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

    private String maskUsername(String username) {
        if (username == null) return "***";
        return username.length() > 3 ? username.substring(0, 2) + "***" : "***";
    }

    private String maskIp(String ip) {
        if (ip == null) return "***";
        int idx = ip.lastIndexOf('.');
        if (idx > 0) return ip.substring(0, idx) + ".***";
        return "***";
    }
}
