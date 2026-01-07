package dev.gamified.GamifiedPlatform.services.email;

import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.security.RateLimitService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final EmailVerificationTokenService tokenService;
    private final RateLimitService rateLimitService;

    // Tempo de validade do token de verificação em horas
    private static final int TOKEN_EXPIRATION_HOURS = 24;

    @Transactional
    public void sendVerificationEmail(User user) {
        // Gera um token único usando UUID
        String token = generateVerificationToken();

        // Calcula quando o token irá expirar (24h a partir de agora)
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(TOKEN_EXPIRATION_HOURS);

        // Atualiza o usuário com o novo token e data de expiração (fallback no banco)
        user.setEmailVerificationToken(token);
        user.setEmailVerificationTokenExpiresAt(expiresAt);
        userRepository.save(user);

        // Armazena o token no Redis para validação rápida
        tokenService.saveToken(user.getId(), token);

        // Delega o envio físico do email para o EmailService
        emailService.sendVerificationEmail(user.getEmail(), user.getUsername(), token);
        log.info("Verification email scheduled for user: {}", user.getUsername());
    }

    @Transactional
    public void verifyEmail(String token) {
        // Primeiro tenta buscar no Redis (mais rápido)
        User user = tokenService.getUserIdByToken(token)
                .flatMap(userRepository::findById)
                .orElseGet(() -> {
                    // Fallback: busca no banco de dados
                    log.debug("Token não encontrado no Redis, buscando no banco");
                    return userRepository.findByEmailVerificationToken(token)
                            .orElseThrow(() -> new BusinessException("Invalid verification token"));
                });

        // Verifica se o email já foi confirmado anteriormente
        if (user.getEmailVerified()) {
            throw new BusinessException("Email already verified");
        }

        // Verifica se o token ainda está dentro do prazo de validade (fallback no banco)
        if (user.getEmailVerificationTokenExpiresAt() != null &&
            user.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Verification token has expired");
        }

        // Ativa a conta do usuário e marca email como verificado
        user.setActive(true);
        user.setEmailVerified(true);

        // Remove o token para que não possa ser reutilizado
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);
        userRepository.save(user);

        // Remove do Redis também
        tokenService.removeToken(token, user.getId());

        log.info("Email verified successfully for user: {}", user.getUsername());
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        // Verifica rate limiting para prevenir spam
        if (!rateLimitService.isEmailVerificationAllowed(email)) {
            long resetTime = rateLimitService.getResetTime("email_verification:" + email);
            throw new BusinessException(
                "Too many verification email requests. Please try again in " + (resetTime / 60) + " minutes"
            );
        }

        // Busca usuário pelo email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));

        // Não permite reenvio se email já foi verificado
        if (user.getEmailVerified()) {
            throw new BusinessException("Email already verified");
        }

        // Gera novo token e reenvia email
        sendVerificationEmail(user);
    }

    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }
}

