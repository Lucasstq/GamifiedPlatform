package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.config.security.PermissionValidator;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.exceptions.InvalidPasswordException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeleteUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(Long userId, String password) {

        PermissionValidator.validateResourceOwnerOrAdmin(userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (Boolean.TRUE.equals(existingUser.getDeleted())) {
            throw new ResourceNotFoundException("User already deleted");
        }

        validatePassword(password, existingUser.getPassword());

        // Soft delete
        existingUser.setDeleted(true);
        existingUser.setDeletedAt(LocalDateTime.now());
        existingUser.setActive(false);
        userRepository.save(existingUser);

        log.info("User {} soft deleted successfully", userId);
    }


    // Validar se a senha fornecida está correta para exclusão do usuário
    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidPasswordException("Invalid password. Cannot delete user.");
        }
    }

}
