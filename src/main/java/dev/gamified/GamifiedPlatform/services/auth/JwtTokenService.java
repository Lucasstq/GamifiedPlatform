package dev.gamified.GamifiedPlatform.services.auth;

import dev.gamified.GamifiedPlatform.domain.RefreshToken;
import dev.gamified.GamifiedPlatform.domain.Scopes;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.repository.RefreshTokenRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.access-token.expiration:900}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration:604800}")
    private Long refreshTokenExpiration;

    public String generateAccessToken(User user) {
        Instant now = Instant.now();

        String scopes = user.getScopes().stream()
                .map(Scopes::getName)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("gamified-platform")
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpiration))
                .claim("userId", user.getId())
                .claim("scope", scopes)
                .claim("role", user.getRole().name())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateRefreshToken(User user, String ipAddress) {
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plusSeconds(refreshTokenExpiration);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiresAt)
                .ipAddress(ipAddress)
                .build();

        refreshTokenRepository.save(refreshToken);
        return token;
    }

}

