package dev.gamified.GamifiedPlatform.controller.notification;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadProfile;
import dev.gamified.GamifiedPlatform.dtos.response.notification.NotificationResponse;
import dev.gamified.GamifiedPlatform.services.notification.GetMyNotificationsService;
import dev.gamified.GamifiedPlatform.services.notification.GetUnreadNotificationsCountService;
import dev.gamified.GamifiedPlatform.services.notification.MarkNotificationsAsReadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Sistema de notificações em tempo real")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final GetMyNotificationsService getMyNotificationsService;
    private final MarkNotificationsAsReadService markNotificationsAsReadService;
    private final GetUnreadNotificationsCountService getUnreadNotificationsCountService;

    @GetMapping
    @CanReadProfile
    @Operation(
            summary = "Listar minhas notificações",
            description = "Retorna uma lista paginada de notificações do usuário autenticado. Pode filtrar apenas não lidas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificações retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<NotificationResponse>> getMyNotifications(
            Pageable pageable,
            @Parameter(description = "Filtrar apenas notificações não lidas")
            @RequestParam(required = false, defaultValue = "false") Boolean onlyUnread) {
        return ResponseEntity.ok(getMyNotificationsService.execute(pageable, onlyUnread));
    }

    @GetMapping("/unread/count")
    @CanReadProfile
    @Operation(
            summary = "Contar notificações não lidas",
            description = "Retorna o número total de notificações não lidas do usuário autenticado"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contagem retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Long.class)))
    })
    public ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(getUnreadNotificationsCountService.execute());
    }

    @PutMapping("/{notificationId}/read")
    @CanReadProfile
    @Operation(
            summary = "Marcar notificação como lida",
            description = "Marca uma notificação específica como lida"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificação marcada como lida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    })
    public ResponseEntity<Void> markAsRead(
            @Parameter(description = "ID da notificação", required = true) @PathVariable Long notificationId) {
        markNotificationsAsReadService.markAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read-all")
    @CanReadProfile
    @Operation(
            summary = "Marcar todas como lidas",
            description = "Marca todas as notificações do usuário autenticado como lidas"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Todas as notificações foram marcadas como lidas")
    })
    public ResponseEntity<Void> markAllAsRead() {
        markNotificationsAsReadService.markAllAsRead();
        return ResponseEntity.noContent().build();
    }
}

