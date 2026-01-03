package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.Scopes;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.UserRequest;
import dev.gamified.GamifiedPlatform.dtos.response.UserResponse;
import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.mapper.UserMapper;
import dev.gamified.GamifiedPlatform.repository.ScopeRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.email.EmailVerificationService;
import dev.gamified.GamifiedPlatform.services.playerCharacter.CreateCharacterForUserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateUserService {

    private final UserRepository userRepository;
    private final ScopeRepository scopeRepository;
    private final CreateCharacterForUserService createCharacterForUser;
    private final EmailVerificationService emailVerificationService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse execute(UserRequest request) {

        validateUserDoesNotExist(request);

        User newUser = buildNewUser(request);

        try {
            User savedUser = userRepository.save(newUser);
            // Cria personagem associado ao usuário
            createCharacterForUser.execute(savedUser);
            // Envia email de confirmação
            emailVerificationService.sendVerificationEmail(savedUser);
            return UserMapper.toResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Username or email already exists");
        }

    }

    private User buildNewUser(UserRequest request) {
        User newUser = UserMapper.toEntity(request);
        newUser.setPassword(passwordEncoder.encode(request.password()));
        newUser.setRole(Roles.ROLE_USER); // Padrão
        newUser.setActive(false);
        newUser.setEmailVerified(false);
        newUser.setScopes(getDefaultScopes(Roles.ROLE_USER));
        return newUser;
    }

    private void validateUserDoesNotExist(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already exists");
        }

    }

    private List<Scopes> getDefaultScopes(Roles role) {
        List<String> scopeNames = getScopeNamesByRole(role);

        List<Scopes> scopes = scopeRepository.findByNameIn(scopeNames);

        if (scopes.size() < scopeNames.size()) {
            List<String> foundNames = scopes.stream()
                    .map(Scopes::getName)
                    .toList();
            List<String> missingScopes = scopeNames.stream()
                    .filter(name -> !foundNames.contains(name))
                    .toList();

            throw new IllegalStateException(
                    "Missing scopes in database: " + missingScopes +
                            ". Please run Flyway migration V5__seed_scopes.sql"
            );
        }

        return scopes;
    }

    private List<String> getScopeNamesByRole(Roles role) {
        return switch (role) {
            case ROLE_USER -> List.of(
                    "profile:read", "profile:write", "profile:delete",
                    "character:read", "character:write",
                    "quests:read", "quests:complete", "achievements:read"
            );
            case ROLE_MENTOR -> List.of(
                    "users:read",
                    "profile:read", "profile:write", "profile:delete",
                    "character:read", "character:write",
                    "quests:read", "quests:write", "quests:complete",
                    "achievements:read"
            );
            case ROLE_ADMIN -> List.of("admin:all");
        };
    }

}
