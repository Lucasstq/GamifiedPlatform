package dev.gamified.GamifiedPlatform.controller;

import dev.gamified.GamifiedPlatform.dtos.request.auth.LoginRequest;
import dev.gamified.GamifiedPlatform.dtos.request.auth.ResendVerificationEmailRequest;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserRequest;
import dev.gamified.GamifiedPlatform.dtos.response.LoginResponse;
import dev.gamified.GamifiedPlatform.dtos.response.UserResponse;
import dev.gamified.GamifiedPlatform.services.auth.AuthService;
import dev.gamified.GamifiedPlatform.services.email.EmailVerificationService;
import dev.gamified.GamifiedPlatform.services.user.CreateUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CreateUserService createUserService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        LoginResponse authenticate = authService.authenticate(request);
        return ResponseEntity.ok(authenticate);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest request) {
        UserResponse response = createUserService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully! You can now login to your account.");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody @Valid ResendVerificationEmailRequest request) {
        emailVerificationService.resendVerificationEmail(request.email());
        return ResponseEntity.ok("Verification email sent successfully!");
    }
}

