package dev.gamified.GamifiedPlatform.services.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    // Email configurado no application.yaml como remetente
    @Value("${spring.mail.username}")
    private String fromEmail;

    // URL base da aplicação para construir links de confirmação/redefinição
    @Value("${app.base-url}")
    private String baseUrl;

    public void sendVerificationEmail(String toEmail, String username, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🎮 Confirm sua conta na Gamified Platform");
            message.setText(buildVerificationEmailBody(username, token));

            // Envia o email através do JavaMailSender configurado
            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String buildVerificationEmailBody(String username, String token) {
        // Monta a URL completa de confirmação concatenando base URL + endpoint + token
        String confirmationUrl = baseUrl + "/auth/verify-email?token=" + token;

        return String.format("""
                Olá, %s! 👋
                
                Bem-vindo à Gamified Platform! 🎮⚔️
                
                Você está a um passo de começar sua jornada épica no mundo Dark Fantasy da programação!
                
                Para ativar sua conta, clique no link abaixo:
                %s
                
                ⚠️ Este link é válido por 24 horas.
                
                Se você não criou uma conta na Gamified Platform, ignore este email.
                
                Que sua jornada seja lendária! 🗡️
                
                ---
                Equipe Gamified Platform
                """, username, confirmationUrl);
    }

    public void sendPasswordResetEmail(String toEmail, String username, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("🔒 Redefinição de senha - Gamified Platform");
            message.setText(buildPasswordResetEmailBody(username, token));

            // Envia o email através do JavaMailSender configurado
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildPasswordResetEmailBody(String username, String token) {
        // Monta a URL completa de redefinição concatenando base URL + endpoint + token
        String resetUrl = baseUrl + "/auth/reset-password?token=" + token;

        return String.format("""
                Olá, %s! 👋
                
                Recebemos uma solicitação para redefinir sua senha na Gamified Platform.
                
                Para criar uma nova senha, clique no link abaixo:
                %s
                
                ⚠️ Este link é válido por 1 hora.
                
                Se você não solicitou a redefinição de senha, ignore este email.
                Sua senha atual permanecerá inalterada.
                
                ---
                Equipe Gamified Platform
                """, username, resetUrl);
    }
}

