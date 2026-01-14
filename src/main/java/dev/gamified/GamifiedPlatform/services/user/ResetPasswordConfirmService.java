package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserChangePasswordRequest;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/*
 * Serviço para confirmar a redefinição de senha de um usuário.
 * Valida o token de redefinição, verifica a expiração, confirma a nova senha e atualiza a senha do usuário.
 */
@Service
@RequiredArgsConstructor
public class ResetPasswordConfirmService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void execute(String token, UserChangePasswordRequest request) {

        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new BusinessException("Invalid token"));

        if (user.getPasswordResetTokenExpiresAt() == null ||
                user.getPasswordResetTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Token has expired");
        }

        if (!request.newPassword().equals(request.confirmNewPassword())) {
            throw new BusinessException("Password confirmation does not match");
        }

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new BusinessException("New password must be different");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiresAt(null);

        userRepository.save(user);
    }
}
