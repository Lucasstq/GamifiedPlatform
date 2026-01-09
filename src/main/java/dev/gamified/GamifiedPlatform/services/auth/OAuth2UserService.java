package dev.gamified.GamifiedPlatform.services.auth;

import dev.gamified.GamifiedPlatform.domain.Scopes;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.enums.AuthProvider;
import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.repository.ScopeRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.playerCharacter.CreateCharacterForUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService {

    private final UserRepository userRepository;
    private final ScopeRepository scopeRepository;
    private final CreateCharacterForUserService createCharacterForUser;

    @Transactional
    public User processOAuth2User(AuthProvider provider, Map<String, Object> attributes) {
        String providerId = extractProviderId(provider, attributes);
        String email = extractEmail(provider, attributes);
        String username = extractUsername(provider, attributes);
        String avatarUrl = extractAvatarUrl(provider, attributes);

        // Verificar se usuário já existe pelo provider
        Optional<User> existingUser = userRepository.findByProviderAndProviderId(provider, providerId);

        if (existingUser.isPresent()) {
            return updateExistingUser(existingUser.get(), email, username, avatarUrl);
        }

        // Verificar se usuário já existe pelo email
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            return linkProviderToExistingUser(userByEmail.get(), provider, providerId, email);
        }

        // Criar novo usuário
        return createNewOAuth2User(provider, providerId, email, username, avatarUrl);
    }

    /**
     * Busca usuário por ID para uso no success handler.
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found after OAuth2 processing"));
    }

    private User updateExistingUser(User user, String email, String username, String avatarUrl) {
        user.setProviderEmail(email);
        if (user.getAvatarUrl() == null || user.getAvatarUrl().isEmpty()) {
            user.setAvatarUrl(avatarUrl);
        }
        log.info("Updating existing OAuth2 user: {}", username);
        return userRepository.save(user);
    }

    private User linkProviderToExistingUser(User user, AuthProvider provider, String providerId, String email) {
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setProviderEmail(email);
        log.info("Linking provider {} to existing user: {}", provider, user.getUsername());
        return userRepository.save(user);
    }

    private User createNewOAuth2User(AuthProvider provider, String providerId, String email,
                                      String username, String avatarUrl) {
        // Garantir username único
        String uniqueUsername = generateUniqueUsername(username);

        User newUser = User.builder()
                .username(uniqueUsername)
                .email(email)
                .provider(provider)
                .providerId(providerId)
                .providerEmail(email)
                .avatarUrl(avatarUrl)
                .active(true)  // OAuth2 users são ativados automaticamente
                .emailVerified(true)  // Email já verificado pelo provider
                .role(Roles.ROLE_USER)
                .scopes(getDefaultScopes())
                .build();

        User savedUser = userRepository.save(newUser);

        // Criar personagem para o novo usuário
        createCharacterForUser.execute(savedUser);

        log.info("Created new OAuth2 user: {} with provider: {}", uniqueUsername, provider);
        return savedUser;
    }

    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    private List<Scopes> getDefaultScopes() {
        List<String> scopeNames = List.of(
                "user:read",
                "user:update",
                "user:delete",
                "character:read",
                "character:update",
                "missions:read",
                "missions:complete",
                "bosses:read",
                "bosses:fight",
                "badges:read",
                "levels:read",
                "grimoire:read",
                "grimoire:create",
                "grimoire:update",
                "grimoire:delete",
                "ranking:read",
                "notifications:read",
                "notifications:delete"
        );

        return scopeRepository.findByNameIn(scopeNames);
    }

    private String extractProviderId(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> (String) attributes.get("sub");
            case GITHUB -> String.valueOf(attributes.get("id"));
            default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
        };
    }

    private String extractEmail(AuthProvider provider, Map<String, Object> attributes) {
        String email = (String) attributes.get("email");

        // Validar se email existe
        if (email == null || email.isEmpty()) {
            if (provider == AuthProvider.GITHUB) {
                // GitHub pode não retornar email se configurado como privado
                // Usar username@github-noreply.com como fallback
                String login = (String) attributes.get("login");
                email = login + "@github-noreply.com";
                log.warn("GitHub user {} has private email. Using fallback: {}", login, email);
            } else {
                throw new IllegalStateException("Email not provided by OAuth2 provider: " + provider);
            }
        }

        return email;
    }

    private String extractUsername(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> extractGoogleUsername(attributes);
            case GITHUB -> (String) attributes.get("login");
            default -> (String) attributes.get("email");
        };
    }

    private String extractGoogleUsername(Map<String, Object> attributes) {
        String email = (String) attributes.get("email");
        if (email != null && email.contains("@")) {
            return email.split("@")[0];
        }
        return (String) attributes.get("name");
    }

    private String extractAvatarUrl(AuthProvider provider, Map<String, Object> attributes) {
        return switch (provider) {
            case GOOGLE -> (String) attributes.get("picture");
            case GITHUB -> (String) attributes.get("avatar_url");
            default -> null;
        };
    }
}

