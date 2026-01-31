package dev.gamified.GamifiedPlatform.services.auth;

import dev.gamified.GamifiedPlatform.domain.RefreshToken;
import dev.gamified.GamifiedPlatform.domain.Scopes;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.enums.ScopeType;
import dev.gamified.GamifiedPlatform.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenService(jwtEncoder, refreshTokenRepository);

        setField(jwtTokenService, "accessTokenExpiration", 900L);
        setField(jwtTokenService, "refreshTokenExpiration", 604800L);
    }

    @Test
    @DisplayName("Deve gerar access token com claims corretas, incluindo scopes do usuário")
    void generateAccessToken_shouldGenerateJwtWithCorrectClaims() {
        User user = baseUserWithScopes();

        Jwt jwtReturnedByEncoder = Jwt.withTokenValue("jwt.token")
                .header("alg", "RS256")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(900))
                .build();

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwtReturnedByEncoder);

        String token = jwtTokenService.generateAccessToken(user);

        assertEquals("jwt.token", token);

        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(captor.capture());

        JwtClaimsSet claims = captor.getValue().getClaims();
        assertNotNull(claims);

        assertEquals("gamified-platform", claims.getClaim("iss"));
        assertEquals(user.getUsername(), claims.getSubject());

        assertEquals(user.getId(), claims.getClaim("userId"));
        assertEquals(user.getRole().name(), claims.getClaim("role"));

        // scope claim: "profile:read quests:complete" (ordem pode variar)
        String scopeClaim = (String) claims.getClaim("scope");
        assertNotNull(scopeClaim);

        List<String> parts = List.of(scopeClaim.split(" "));
        assertTrue(parts.contains(ScopeType.PROFILE_READ.getScope()));
        assertTrue(parts.contains(ScopeType.QUESTS_COMPLETE.getScope()));

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiresAt());
    }

    @Test
    @DisplayName("Deve gerar refresh token e persistir no repositório com usuário e IP")
    void generateRefreshToken_shouldCreateAndPersistRefreshToken() {
        User user = baseUserNoScopes();
        String ip = "192.168.0.1";

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        String token = jwtTokenService.generateRefreshToken(user, ip);

        assertNotNull(token);
        assertFalse(token.isBlank());

        verify(refreshTokenRepository).save(captor.capture());

        RefreshToken saved = captor.getValue();
        assertNotNull(saved);

        assertEquals(token, saved.getToken());
        assertEquals(user, saved.getUser());
        assertEquals(ip, saved.getIpAddress());
        assertNotNull(saved.getExpiryDate());
        assertTrue(saved.getExpiryDate().isAfter(Instant.now()), "Expiry date deve estar no futuro");
    }

    @Test
    @DisplayName("Deve gerar refresh tokens diferentes a cada chamada")
    void generateRefreshToken_shouldGenerateUniqueTokens() {
        User user = baseUserNoScopes();

        String token1 = jwtTokenService.generateRefreshToken(user, "1.1.1.1");
        String token2 = jwtTokenService.generateRefreshToken(user, "1.1.1.1");

        assertNotEquals(token1, token2);
        verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
    }

    private User baseUserNoScopes() {
        User user = new User();
        user.setId(1L);
        user.setUsername("lucas");
        user.setRole(Roles.ROLE_USER);
        user.setScopes(List.of());
        return user;
    }

    private User baseUserWithScopes() {
        User user = baseUserNoScopes();

        Scopes profileRead = new Scopes();
        profileRead.setName(ScopeType.PROFILE_READ.getScope());

        Scopes questsComplete = new Scopes();
        questsComplete.setName(ScopeType.QUESTS_COMPLETE.getScope());

        // List<Scopes>
        user.setScopes(List.of(profileRead, questsComplete));
        return user;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field '" + fieldName + "' via reflection", e);
        }
    }
}
