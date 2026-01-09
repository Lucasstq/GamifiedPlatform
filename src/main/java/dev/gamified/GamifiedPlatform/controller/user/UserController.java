package dev.gamified.GamifiedPlatform.controller.user;

import dev.gamified.GamifiedPlatform.config.annotations.CanDeleteProfile;
import dev.gamified.GamifiedPlatform.config.annotations.CanReadUsers;
import dev.gamified.GamifiedPlatform.config.annotations.CanWriteProfile;
import dev.gamified.GamifiedPlatform.dtos.request.user.DeleteUserRequest;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserChangePasswordRequest;
import dev.gamified.GamifiedPlatform.dtos.request.user.UserUpdateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.user.PublicUserProfileResponse;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserResponse;
import dev.gamified.GamifiedPlatform.services.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Gerenciamento de perfis e informações de usuário")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UpdateUserService updateUser;
    private final UserByIdService getUserById;
    private final UserByUsernameService getUserByUsername;
    private final UserChangePasswordService changePasswordService;
    private final DeleteUserService deleteUser;
    private final GetPublicUserProfileService getPublicUserProfileService;

    @PutMapping("/{id}")
    @CanWriteProfile
    @Operation(
            summary = "Atualizar usuário",
            description = "Atualiza informações do perfil do usuário. Requer permissão 'profile:write'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "403", description = "Sem permissão para atualizar este usuário"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(updateUser.execute(id, request));
    }

    @GetMapping("/{id}")
    @CanReadUsers
    @Operation(
            summary = "Buscar usuário por ID",
            description = "Retorna informações detalhadas de um usuário específico. Requer permissão 'users:read'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(getUserById.execute(id));
    }

    @GetMapping("/search")
    @CanReadUsers
    @Operation(
            summary = "Buscar usuário por username",
            description = "Busca um usuário pelo nome de usuário. Requer permissão 'users:read'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuário encontrado",
                    content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UserResponse> getUserByUsername(
            @Parameter(description = "Nome de usuário", required = true) @RequestParam String username) {
        return ResponseEntity.ok(getUserByUsername.execute(username));
    }

    @GetMapping("/{id}/profile")
    @CanReadUsers
    @Operation(
            summary = "Buscar perfil público",
            description = "Retorna o perfil público de um usuário com estatísticas de progresso. Requer permissão 'users:read'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil encontrado",
                    content = @Content(schema = @Schema(implementation = PublicUserProfileResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<PublicUserProfileResponse> getPublicProfile(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(getPublicUserProfileService.execute(id));
    }

    @PatchMapping("/{id}/change-password")
    @CanWriteProfile
    @Operation(
            summary = "Alterar senha",
            description = "Altera a senha do usuário. Requer a senha atual e a nova senha. Requer permissão 'profile:write'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou nova senha inválida"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para alterar senha deste usuário")
    })
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long id,
            @Valid @RequestBody UserChangePasswordRequest request) {
        changePasswordService.execute(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    @CanDeleteProfile
    @Operation(
            summary = "Deletar usuário (soft delete)",
            description = "Remove logicamente um usuário do sistema. Requer confirmação de senha. Requer permissão 'profile:delete'."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha incorreta"),
            @ApiResponse(responseCode = "403", description = "Sem permissão para deletar este usuário")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID do usuário", required = true) @PathVariable Long id,
            @Valid @RequestBody DeleteUserRequest request) {
        deleteUser.execute(id, request.password());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
