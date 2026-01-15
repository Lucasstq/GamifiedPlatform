package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.config.security.PermissionValidator;
import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserAuthenticateChangePasswordRequest;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/*
 * Serviço para alterar a senha de um usuário autenticado.
 */
@Service
@RequiredArgsConstructor
public class UserChangePasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(Long userId, UserAuthenticateChangePasswordRequest request) {

        PermissionValidator.validateResourceOwnerOrAdmin(userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        validatePasswordConfirmation(request.newPassword(), request.confirmNewPassword());
        validatePasswordIsDifferent(request.newPassword(), existingUser.getPassword());

        existingUser.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(existingUser);
    }

    /*
    Valida se a nova senha é diferente da senha atual do usuário.
    Lança uma BusinessException se as senhas forem iguais.
     */

    private void validatePasswordIsDifferent(String newPassword, String oldPassword) {
        if (passwordEncoder.matches(newPassword, oldPassword)) {
            throw new BusinessException("New password must be different from the current password");
        }
    }

    private void validatePasswordConfirmation(String newPassword, String confirmNewPassword) {
        if (!newPassword.equals(confirmNewPassword)) {
            throw new BusinessException("Password confirmation does not match");
        }
    }

}
