package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserChangePasswordRequest;
import dev.gamified.GamifiedPlatform.exceptions.AcessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserChangePasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(Long userId, UserChangePasswordRequest request) {

        isOwnerOrAdmin(userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourseNotFoundException("User not found"));

        validateCurrentPassword(request.currentPassword(), existingUser.getPassword());
        validatePasswordIsDifferent(existingUser.getPassword(), request.newPassword());

        existingUser.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(existingUser);
    }

    // Verifica se o usuário autenticado tem permissão para atualizar
    private void isOwnerOrAdmin(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AcessDeniedException("You do not have permission to update this user");
        }
    }

    /*
    Valida se a senha atual fornecida corresponde à senha armazenada do usuário.
    Lança uma BusinessException se a senha estiver incorreta.
     */
    private void validateCurrentPassword(String currentPassword, String storedPassword) {
        if (!passwordEncoder.matches(currentPassword, storedPassword)) {
            throw new BusinessException("Current password is incorrect");
        }
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

}
