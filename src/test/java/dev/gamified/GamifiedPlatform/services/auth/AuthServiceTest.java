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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private RateLimitService rateLimitService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private SecurityAuditService auditService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtTokenService,
                rateLimitService,
                refreshTokenService,
                auditService
        );
    }

    @Test
    @DisplayName("Deve bloquear login quando rate limit for excedido")
    void authenticate_shouldThrowBusinessException_whenRateLimitExceeded_andLogSuspicious() {
        LoginRequest req = new LoginRequest("lucas", "123");
        String ip = "1.2.3.4";
        String agent = "Chrome";

        when(rateLimitService.isLoginAllowed("lucas")).thenReturn(false);
        when(rateLimitService.getResetTime("login:lucas")).thenReturn(600L); // 10 min

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.authenticate(req, ip, agent));

        assertTrue(ex.getMessage().contains("Too many login attempts"));
        assertTrue(ex.getMessage().contains("10"));

        verify(auditService).logSuspiciousActivity(
                isNull(),
                eq("lucas"),
                eq(ip),
                contains("Rate limit exceeded")
        );

        verifyNoInteractions(userRepository, passwordEncoder, jwtTokenService, refreshTokenService);
        verify(rateLimitService, never()).reset(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for encontrado")
    void authenticate_shouldThrowBusinessException_whenUserNotFound_andLogFailure() {
        LoginRequest req = new LoginRequest("ghost", "123");
        String ip = "1.2.3.4";
        String agent = "Chrome";

        when(rateLimitService.isLoginAllowed("ghost")).thenReturn(true);
        when(userRepository.findUserByUsername("ghost")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.authenticate(req, ip, agent));

        assertEquals("Invalid username or password", ex.getMessage());

        verify(auditService).logLoginFailure(
                eq("ghost"),
                eq(ip),
                eq(agent),
                contains("User not found")
        );

        verify(rateLimitService, never()).reset(anyString());
        verifyNoInteractions(passwordEncoder, jwtTokenService, refreshTokenService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando provider do usuário estiver nulo")
    void authenticate_shouldThrowBusinessException_whenProviderIsNull_andLogSuspicious() {
        LoginRequest req = new LoginRequest("lucas", "123");
        String ip = "1.2.3.4";
        String agent = "Chrome";

        User user = baseUser();
        user.setProvider(null);

        when(rateLimitService.isLoginAllowed("lucas")).thenReturn(true);
        when(userRepository.findUserByUsername("lucas")).thenReturn(Optional.of(user));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.authenticate(req, ip, agent));

        assertTrue(ex.getMessage().contains("Account configuration error"));

        verify(auditService).logSuspiciousActivity(
                eq(user.getId()),
                eq(user.getUsername()),
                eq(ip),
                contains("NULL provider")
        );

        verifyNoInteractions(passwordEncoder, jwtTokenService, refreshTokenService);
        verify(rateLimitService, never()).reset(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha for inválida")
    void authenticate_shouldThrowBusinessException_whenPasswordIsWrong_andLogFailure() {
        LoginRequest req = new LoginRequest("lucas", "wrong");
        String ip = "1.2.3.4";
        String agent = "Chrome";

        User user = baseUser();

        when(rateLimitService.isLoginAllowed("lucas")).thenReturn(true);
        when(userRepository.findUserByUsername("lucas")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", user.getPassword())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.authenticate(req, ip, agent));

        assertEquals("Invalid username or password", ex.getMessage());

        verify(auditService).logLoginFailure(
                eq("lucas"),
                eq(ip),
                eq(agent),
                eq("Invalid username or password")
        );

        verify(rateLimitService, never()).reset(anyString());
        verifyNoInteractions(jwtTokenService, refreshTokenService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando email do usuário não estiver verificado")
    void authenticate_shouldThrowBusinessException_whenEmailNotVerified_andLogFailure() {
        LoginRequest req = new LoginRequest("lucas", "123");
        String ip = "1.2.3.4";
        String agent = "Chrome";

        User user = baseUser();
        user.setEmailVerified(false);

        when(rateLimitService.isLoginAllowed("lucas")).thenReturn(true);
        when(userRepository.findUserByUsername("lucas")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", user.getPassword())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.authenticate(req, ip, agent));

        assertEquals("Email address is not verified", ex.getMessage());

        verify(auditService).logLoginFailure(
                eq("lucas"),
                eq(ip),
                eq(agent),
                eq("Email address is not verified")
        );

        verify(rateLimitService, never()).reset(anyString());
        verifyNoInteractions(jwtTokenService, refreshTokenService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando conta do usuário estiver inativa")
    void authenticate_shouldThrowBusinessException_whenUserInactive_andLogFailure() {
        LoginRequest req = new LoginRequest("lucas", "123");
        String ip = "1.2.3.4";
        String agent = "Chrome";

        User user = baseUser();
        user.setActive(false);

        when(rateLimitService.isLoginAllowed("lucas")).thenReturn(true);
        when(userRepository.findUserByUsername("lucas")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", user.getPassword())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.authenticate(req, ip, agent));

        assertEquals("User account is inactive", ex.getMessage());

        verify(auditService).logLoginFailure(
                eq("lucas"),
                eq(ip),
                eq(agent),
                eq("User account is inactive")
        );

        verify(rateLimitService, never()).reset(anyString());
        verifyNoInteractions(jwtTokenService, refreshTokenService);
    }

    @Test
    @DisplayName("Deve autenticar usuário com sucesso e gerar tokens")
    void authenticate_shouldReturnLoginResponse_whenSuccess_andResetRateLimit_andLogSuccess() {
        LoginRequest req = new LoginRequest("lucas", "123");
        String ip = "1.2.3.4";
        String agent = "Chrome";

        User user = baseUser();

        when(rateLimitService.isLoginAllowed("lucas")).thenReturn(true);
        when(userRepository.findUserByUsername("lucas")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123", user.getPassword())).thenReturn(true);

        when(jwtTokenService.generateAccessToken(user)).thenReturn("access.jwt");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900L);
        when(jwtTokenService.getRefreshTokenExpiration()).thenReturn(604800L);

        RefreshToken rt = new RefreshToken();
        rt.setToken("refresh.jwt");
        rt.setUser(user);
        rt.setUserAgent(agent);

        when(refreshTokenService.createRefreshToken(user, ip, agent)).thenReturn(rt);

        LoginResponse resp = authService.authenticate(req, ip, agent);

        assertNotNull(resp);
        assertEquals("access.jwt", resp.accessToken());
        assertEquals("refresh.jwt", resp.refreshToken());
        assertEquals("Bearer", resp.tokenType());
        assertEquals(900L, resp.expiresIn());
        assertEquals(604800L, resp.refreshExpiresIn());

        verify(rateLimitService).reset("login:lucas");
        verify(jwtTokenService).generateAccessToken(user);
        verify(refreshTokenService).createRefreshToken(user, ip, agent);
        verify(auditService).logLoginSuccess(user.getId(), user.getUsername(), ip, agent);
    }

    @Test
    @DisplayName("Deve resetar contador de tentativas após login bem-sucedido")
    void refreshAccessToken_shouldRotateToken_andLogRefresh_whenSameUserAgent() {
        String oldValue = "old.refresh";
        String ip = "1.2.3.4";
        String agent = "Chrome";

        User user = baseUser();

        RefreshToken oldToken = new RefreshToken();
        oldToken.setToken(oldValue);
        oldToken.setUser(user);
        oldToken.setRevoked(false);
        oldToken.setUserAgent(agent);

        when(refreshTokenService.validateRefreshToken(oldValue)).thenReturn(oldToken);

        RefreshToken newToken = new RefreshToken();
        newToken.setToken("new.refresh");
        newToken.setUser(user);
        newToken.setUserAgent(agent);

        when(refreshTokenService.createRefreshToken(user, ip, agent)).thenReturn(newToken);

        when(jwtTokenService.generateAccessToken(user)).thenReturn("new.access");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900L);
        when(jwtTokenService.getRefreshTokenExpiration()).thenReturn(604800L);

        LoginResponse resp = authService.refreshAccessToken(oldValue, ip, agent);

        assertEquals("new.access", resp.accessToken());
        assertEquals("new.refresh", resp.refreshToken());

        assertTrue(oldToken.isRevoked());
        verify(refreshTokenService).saveRevokedToken(oldToken);
        verify(auditService).logTokenRefresh(user.getId(), user.getUsername(), ip);
        verify(auditService, never()).logSuspiciousActivity(anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve registrar atividade suspeita ao renovar token com user agent diferente")
    void refreshAccessToken_shouldLogSuspicious_whenDifferentUserAgent() {
        String oldValue = "old.refresh";
        String ip = "1.2.3.4";
        String oldAgent = "Chrome";
        String newAgent = "Firefox";

        User user = baseUser();

        RefreshToken oldToken = new RefreshToken();
        oldToken.setToken(oldValue);
        oldToken.setUser(user);
        oldToken.setRevoked(false);
        oldToken.setUserAgent(oldAgent);

        when(refreshTokenService.validateRefreshToken(oldValue)).thenReturn(oldToken);

        RefreshToken newToken = new RefreshToken();
        newToken.setToken("new.refresh");
        newToken.setUser(user);
        newToken.setUserAgent(newAgent);

        when(refreshTokenService.createRefreshToken(user, ip, newAgent)).thenReturn(newToken);

        when(jwtTokenService.generateAccessToken(user)).thenReturn("new.access");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900L);
        when(jwtTokenService.getRefreshTokenExpiration()).thenReturn(604800L);

        LoginResponse resp = authService.refreshAccessToken(oldValue, ip, newAgent);

        assertEquals("new.access", resp.accessToken());
        assertEquals("new.refresh", resp.refreshToken());

        verify(auditService).logSuspiciousActivity(
                eq(user.getId()),
                eq(user.getUsername()),
                eq(ip),
                contains("different user agent")
        );
        verify(auditService).logTokenRefresh(user.getId(), user.getUsername(), ip);
    }

    @Test
    @DisplayName("Deve revogar refresh token no logout")
    void logout_shouldRevokeRefreshToken_andLogLogout() {
        String tokenValue = "refresh.jwt";
        String ip = "1.2.3.4";

        User user = baseUser();

        RefreshToken rt = new RefreshToken();
        rt.setToken(tokenValue);
        rt.setUser(user);

        when(refreshTokenService.validateRefreshToken(tokenValue)).thenReturn(rt);

        authService.logout(tokenValue, ip);

        verify(refreshTokenService).revokeRefreshToken(tokenValue);
        verify(auditService).logLogout(user.getId(), user.getUsername(), ip);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário autenticado não existir")
    void logoutAllDevices_shouldThrowAccessDenied_whenNotAuthenticated() {
        String ip = "1.2.3.4";

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.empty());

            assertThrows(AccessDeniedException.class,
                    () -> authService.logoutAllDevices(ip));
        }

        verifyNoInteractions(userRepository, refreshTokenService, auditService);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não estiver autenticado")
    void logoutAllDevices_shouldThrowBusinessException_whenUserNotFound() {
        String ip = "1.2.3.4";
        long userId = 10L;

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> authService.logoutAllDevices(ip));

            assertEquals("User not found", ex.getMessage());
        }

        verify(refreshTokenService, never()).revokeAllUserTokens(anyLong());
        verify(auditService, never()).logLogoutAllDevices(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve revogar todos os refresh tokens do usuário")
    void logoutAllDevices_shouldRevokeAllTokens_andLogLogoutAllDevices() {
        String ip = "1.2.3.4";
        long userId = 10L;

        User user = baseUser();
        user.setId(userId);

        try (MockedStatic<SecurityUtils> mocked = mockStatic(SecurityUtils.class)) {
            mocked.when(SecurityUtils::getCurrentUserId).thenReturn(Optional.of(userId));

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            authService.logoutAllDevices(ip);
        }

        verify(refreshTokenService).revokeAllUserTokens(userId);
        verify(auditService).logLogoutAllDevices(user.getId(), user.getUsername(), ip);
    }

    private User baseUser() {
        User u = new User();
        u.setId(1L);
        u.setUsername("lucas");
        u.setPassword("$2a$10$encoded");
        u.setActive(true);
        u.setEmailVerified(true);

        u.setProvider(dev.gamified.GamifiedPlatform.enums.AuthProvider.LOCAL);

        return u;
    }
}
