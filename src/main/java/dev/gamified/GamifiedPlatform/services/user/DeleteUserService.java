package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.InvalidPasswordException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.playerCharacter.DeletePlayerCharacterService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DeletePlayerCharacterService deleteCharacter;

    @Transactional
    public void execute(Long userId, String password) {

        isOwnerOrAdmin(userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        validatePassword(password, existingUser.getPassword());
        deleteAssociatedCharacter(existingUser);

        userRepository.delete(existingUser);
    }

    // Verifica se o usuário autenticado tem permissão para deletar
    private void isOwnerOrAdmin(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AccessDeniedException("You do not have permission to delete this user");
        }
    }

    // Validar se a senha fornecida está correta para exclusão do usuário
    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new InvalidPasswordException("Invalid password. Cannot delete user.");
        }
    }

    // Deletar o personagem associado se existir
    private void deleteAssociatedCharacter(User user) {
        if (user.getPlayerCharacter() != null) {
            deleteCharacter.execute(user.getPlayerCharacter());
        }
    }

}
