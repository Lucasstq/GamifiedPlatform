package dev.gamified.GamifiedPlatform.services.auth;

import dev.gamified.GamifiedPlatform.domain.Scopes;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.LoginRequest;
import dev.gamified.GamifiedPlatform.dtos.response.LoginResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findUserByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        isPasswordCorrect(request.password(), user.getPassword());
        isEmailVerified(user);
        isUserActive(user);

        String token = generateToken(user);

        return LoginResponse.builder()
                .token(token)
                .expiresIn(3600L + " seconds")
                .build();
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

    private String generateToken(User user) {

        List<String> scopes = user.getScopes().stream()
                .map(Scopes::getName)
                .toList();
        long expiresIn = 3600L;


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("gamified-platform")
                .subject(user.getUsername())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(expiresIn))
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("scope", scopes)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}

