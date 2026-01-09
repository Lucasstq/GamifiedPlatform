package dev.gamified.GamifiedPlatform.controller.auth;

import dev.gamified.GamifiedPlatform.dtos.request.auth.LoginRequest;
import dev.gamified.GamifiedPlatform.dtos.request.auth.RefreshTokenRequest;
import dev.gamified.GamifiedPlatform.dtos.request.auth.ResendVerificationEmailRequest;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserRequest;
import dev.gamified.GamifiedPlatform.dtos.response.login.LoginResponse;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserResponse;
import dev.gamified.GamifiedPlatform.services.auth.AuthService;
import dev.gamified.GamifiedPlatform.services.email.EmailVerificationService;
import dev.gamified.GamifiedPlatform.services.user.CreateUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para autenticação, registro e gerenciamento de sessão")
public class AuthController {

    private final AuthService authService;
    private final CreateUserService createUserService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/login")
    @Operation(
            summary = "Fazer login",
            description = "Autentica um usuário com username e password, retornando tokens JWT de acesso e refresh"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Conta não verificada ou inativa",
                    content = @Content)
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request,
                                               HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        LoginResponse response = authService.authenticate(request, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Renovar token de acesso",
            description = "Gera um novo access token usando o refresh token válido. O access token expira em 15 minutos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token renovado com sucesso",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token inválido ou expirado",
                    content = @Content)
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request,
                                                       HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        LoginResponse response = authService.refreshAccessToken(request.refreshToken(), ipAddress);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Fazer logout",
            description = "Invalida o refresh token fornecido, encerrando a sessão do dispositivo atual"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Token inválido")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<String> logout(@RequestBody @Valid RefreshTokenRequest request,
                                         HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        authService.logout(request.refreshToken(), ipAddress);
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/logout-all-devices")
    @Operation(
            summary = "Fazer logout de todos os dispositivos",
            description = "Invalida todos os refresh tokens do usuário autenticado, encerrando todas as sessões ativas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout de todos os dispositivos realizado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<String> logoutAllDevices(HttpServletRequest httpRequest) {
        String ipAddress = getClientIp(httpRequest);
        authService.logoutAllDevices(ipAddress);
        return ResponseEntity.ok("Logged out from all devices successfully");
    }

    @PostMapping("/register")
    @Operation(
            summary = "Registrar novo usuário",
            description = "Cria uma nova conta de usuário. Um email de verificação será enviado para o endereço fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou username/email já em uso",
                    content = @Content)
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid UserRequest request) {
        UserResponse response = createUserService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/verify-email")
    @Operation(
            summary = "Verificar email",
            description = "Valida o email do usuário através do token enviado por email"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verificado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Token inválido ou expirado")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<String> verifyEmail(
            @Parameter(description = "Token de verificação recebido por email", required = true)
            @RequestParam String token) {
        emailVerificationService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully! You can now login to your account.");
    }

    @PostMapping("/resend-verification")
    @Operation(
            summary = "Reenviar email de verificação",
            description = "Reenvia o email de verificação para o endereço fornecido"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email de verificação reenviado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Email não encontrado ou já verificado")
    })
    @SecurityRequirement(name = "")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody @Valid ResendVerificationEmailRequest request) {
        emailVerificationService.resendVerificationEmail(request.email());
        return ResponseEntity.ok("Verification email sent successfully!");
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}

