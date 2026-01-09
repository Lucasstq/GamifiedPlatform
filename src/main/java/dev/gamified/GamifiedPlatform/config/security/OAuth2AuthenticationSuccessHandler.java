package dev.gamified.GamifiedPlatform.config.security;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.enums.AuthProvider;
import dev.gamified.GamifiedPlatform.services.auth.OAuth2UserService;
import dev.gamified.GamifiedPlatform.services.security.SecurityAuditService;
import dev.gamified.GamifiedPlatform.services.auth.JwtTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2UserService oAuth2UserService;
    private final JwtTokenService jwtTokenService;
    private final SecurityAuditService securityAuditService;

    @Value("${app.oauth2.redirect-uri:http://localhost:3000/oauth2/redirect}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            try {
                OAuth2User oAuth2User = oauthToken.getPrincipal();

                // Validar atributos
                Map<String, Object> attributes = oAuth2User.getAttributes();
                if (attributes == null || attributes.isEmpty()) {
                    log.error("OAuth2 attributes are null or empty");
                    handleError(response, "Invalid OAuth2 response - no attributes");
                    return;
                }

                // Obter dados do usuário já processados pelo CustomOAuth2UserServiceAdapter
                Object userIdObj = attributes.get("userId");
                Object usernameObj = attributes.get("username");

                if (userIdObj == null || usernameObj == null) {
                    log.error("OAuth2 userId or username is missing. Attributes: {}", attributes.keySet());
                    handleError(response, "Invalid OAuth2 response - missing user data");
                    return;
                }

                Long userId = ((Number) userIdObj).longValue();
                String username = (String) usernameObj;
                String registrationId = oauthToken.getAuthorizedClientRegistrationId();
                AuthProvider provider = AuthProvider.valueOf(registrationId.toUpperCase());

                log.info("Processing OAuth2 success for user: {} (ID: {}) via provider: {}",
                         username, userId, provider);

                // Buscar usuário completo para gerar tokens
                User user = oAuth2UserService.getUserById(userId);

                // Gerar tokens JWT
                String accessToken = jwtTokenService.generateAccessToken(user);
                String refreshToken = jwtTokenService.generateRefreshToken(user, getClientIp(request));

                // Registrar log de auditoria
                securityAuditService.logLoginSuccess(
                        user.getId(),
                        user.getUsername(),
                        getClientIp(request),
                        request.getHeader("User-Agent")
                );

                log.info("OAuth2 login successful for user: {} via provider: {}", user.getUsername(), provider);

                // Redirecionar para o frontend com tokens
                String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                        .queryParam("accessToken", accessToken)
                        .queryParam("refreshToken", refreshToken)
                        .queryParam("tokenType", "Bearer")
                        .build().toUriString();

                getRedirectStrategy().sendRedirect(request, response, targetUrl);

            } catch (Exception e) {
                log.error("Error processing OAuth2 authentication success", e);
                handleError(response, "Error processing OAuth2 login: " + e.getMessage());
            }
        } else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    private void handleError(HttpServletResponse response, String errorMessage) throws IOException {
        log.error("OAuth2 error: {}", errorMessage);
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("error", errorMessage)
                .build().toUriString();
        response.sendRedirect(targetUrl);
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}

