package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserForgetPasswordRequest;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.email.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/*
 * Serviço para iniciar o processo de recuperação de senha de um usuário.
 * Gera um token de redefinição de senha, define sua expiração e envia um email com o link de redefinição.
 */
@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public void execute(UserForgetPasswordRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("If the email exists, a reset link was sent"));

        String token = UUID.randomUUID().toString();

        user.setPasswordResetToken(token);

        user.setPasswordResetTokenExpiresAt(LocalDateTime.now().plusHours(1));

        userRepository.save(user);

        emailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getUsername(),
                token
        );
    }
}
