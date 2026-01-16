package dev.gamified.GamifiedPlatform.services.auth;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.auth.LoginRequest;
import dev.gamified.GamifiedPlatform.dtos.response.login.LoginResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.security.RateLimitService;
import dev.gamified.GamifiedPlatform.services.security.SecurityAuditService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenService jwtTokenService;
    @Mock
    private RateLimitService rateLimitService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private SecurityAuditService auditService;
    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Login válido retorna LoginResponse e mascara dados")
    void testLoginValido() {
        User user = new User();
        user.setId(1L);
        user.setUsername("usuario123");
        user.setPassword("encoded");
        user.setActive(true);
        user.setEmailVerified(true);
        user.setProvider(user.getProvider());
        LoginRequest req = new LoginRequest("usuario123", "senha");
        when(rateLimitService.isLoginAllowed(any())).thenReturn(true);
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtTokenService.generateAccessToken(any())).thenReturn("token");
        when(refreshTokenService.createRefreshToken(any(), any(), any())).thenReturn(null);
        LoginResponse resp = authService.authenticate(req, "192.168.1.10", "agent");
        assertNotNull(resp);
    }

    @Test
    @DisplayName("Login bloqueado por rate limit")
    void testLoginRateLimit() {
        LoginRequest req = new LoginRequest("usuario123", "senha");
        when(rateLimitService.isLoginAllowed(any())).thenReturn(false);
        assertThrows(BusinessException.class, () -> authService.authenticate(req, "ip", "agent"));
    }

    @Test
    @DisplayName("Login com senha errada")
    void testLoginSenhaErrada() {
        User user = new User();
        user.setId(1L);
        user.setUsername("usuario123");
        user.setPassword("encoded");
        user.setActive(true);
        user.setEmailVerified(true);
        user.setProvider(user.getProvider());
        LoginRequest req = new LoginRequest("usuario123", "senha");
        when(rateLimitService.isLoginAllowed(any())).thenReturn(true);
        when(userRepository.findUserByUsername(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);
        assertThrows(BusinessException.class, () -> authService.authenticate(req, "ip", "agent"));
    }
}
