package dev.gamified.GamifiedPlatform.config.security;

import dev.gamified.GamifiedPlatform.enums.Roles;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;

/**
 * Utility class for security operations.
 * Provides simple access to authentication context and authorization checks.
 */
public final class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static Optional<String> getCurrentUsername() {
        return getJwt().map(Jwt::getSubject);
    }

    public static Optional<Long> getCurrentUserId() {
        return getJwt()
                .map(jwt -> jwt.getClaim("userId"))
                .filter(Number.class::isInstance)
                .map(Number.class::cast)
                .map(Number::longValue);
    }

    public static boolean isAuthenticated() {
        return getAuthentication()
                .filter(Authentication::isAuthenticated)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .isPresent();
    }

    public static boolean hasRole(Roles role) {
        return role != null && hasAuthority(role.name());
    }

    public static boolean hasAuthority(String authority) {
        return getAuthentication()
                .map(auth -> auth.getAuthorities().stream()
                        .anyMatch(a -> authority.equals(a.getAuthority())))
                .orElse(false);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean isResourceOwnerOrAdmin(Long resourceOwnerId) {
        if (resourceOwnerId == null) return false;

        return hasRole(Roles.ROLE_ADMIN)
                || getCurrentUserId().map(resourceOwnerId::equals).orElse(false);
    }

    private static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    private static Optional<Jwt> getJwt() {
        return getAuthentication()
                .filter(JwtAuthenticationToken.class::isInstance)
                .map(JwtAuthenticationToken.class::cast)
                .map(JwtAuthenticationToken::getToken);
    }
}

