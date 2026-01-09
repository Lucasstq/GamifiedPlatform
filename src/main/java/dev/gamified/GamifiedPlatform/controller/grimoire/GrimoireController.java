package dev.gamified.GamifiedPlatform.controller.grimoire;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadUsers;
import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.response.grimoire.GrimoireResponse;
import dev.gamified.GamifiedPlatform.services.grimoire.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Controller para gerenciar grimórios (PDFs educacionais).
 * Grimórios são desbloqueados ao atingir determinado nível.
 */
@RestController
@RequestMapping("/grimoires")
@RequiredArgsConstructor
@Tag(name = "Grimórios", description = "Materiais educacionais e PDFs desbloqueáveis por nível")
@SecurityRequirement(name = "bearerAuth")
public class GrimoireController {

    private final GetAllGrimoiresService getAllGrimoiresService;
    private final GetGrimoireInfoService getGrimoireInfoService;
    private final DownloadGrimoireService downloadGrimoireService;
    private final UploadGrimoireService uploadGrimoireService;
    private final DeleteGrimoireService deleteGrimoireService;

    @GetMapping
    @CanReadUsers
    @Operation(
            summary = "Listar todos os grimórios",
            description = "Retorna uma lista paginada de todos os grimórios disponíveis, indicando quais o usuário pode acessar baseado no seu nível"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de grimórios retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<GrimoireResponse>> listGrimoires(
            @PageableDefault(size = 20, sort = "uploadedAt") Pageable pageable) {
        return ResponseEntity.ok(getAllGrimoiresService.execute(pageable));
    }

    @GetMapping("/{levelId}")
    @CanReadUsers
    @Operation(
            summary = "Buscar informações do grimório",
            description = "Retorna as informações de um grimório específico associado a um nível"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Grimório encontrado",
                    content = @Content(schema = @Schema(implementation = GrimoireResponse.class))),
            @ApiResponse(responseCode = "404", description = "Grimório não encontrado para este nível")
    })
    public ResponseEntity<GrimoireResponse> getGrimoireInfo(
            @Parameter(description = "ID do nível", required = true) @PathVariable Long levelId) {
        return ResponseEntity.ok(getGrimoireInfoService.execute(levelId));
    }

    @GetMapping("/{levelId}/download")
    @CanReadUsers
    @Operation(
            summary = "Download do grimório",
            description = "Faz download de um grimório em PDF. Valida se o usuário atingiu o nível necessário para desbloqueá-lo."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Download iniciado com sucesso",
                    content = @Content(mediaType = "application/pdf")),
            @ApiResponse(responseCode = "403", description = "Usuário não possui nível suficiente"),
            @ApiResponse(responseCode = "404", description = "Grimório não encontrado")
    })
    public ResponseEntity<InputStreamResource> downloadGrimoire(
            @Parameter(description = "ID do nível", required = true) @PathVariable Long levelId) {

        InputStream grimoireStream = downloadGrimoireService.execute(levelId);

        // Busca informações do grimório para o nome do arquivo
        GrimoireResponse grimoire = getGrimoireInfoService.execute(levelId);

        // SEGURANÇA: Sanitizar nome do arquivo para prevenir path traversal
        String safeFilename = sanitizeFilename(grimoire.originalName());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename(safeFilename, StandardCharsets.UTF_8)
                        .build()
        );
        headers.setContentType(MediaType.APPLICATION_PDF);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new InputStreamResource(grimoireStream));
    }

    /*
     *SEGURANÇA: Sanitiza nome de arquivo para prevenir path traversal e injection
     */
    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "grimoire.pdf";
        }

        // Remove path traversal characters
        String safe = filename.replaceAll("[./\\\\]", "");

        // Remove caracteres especiais perigosos (mantém apenas alfanuméricos, espaços, hífens e underscores)
        safe = safe.replaceAll("[^a-zA-Z0-9._\\-\\s]", "");

        // Remove espaços múltiplos
        safe = safe.replaceAll("\\s+", " ").trim();

        // Limita tamanho
        if (safe.length() > 100) {
            safe = safe.substring(0, 100);
        }

        // Garante extensão .pdf
        if (!safe.toLowerCase().endsWith(".pdf")) {
            safe += ".pdf";
        }

        // Fallback se ficou vazio após sanitização
        return safe.isBlank() ? "grimoire.pdf" : safe;
    }

    @PostMapping("/admin/{levelId}")
    @IsAdmin
    @Operation(
            summary = "Upload de grimório (Admin)",
            description = "Faz upload de um grimório em PDF para um nível específico. Apenas administradores podem fazer upload."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grimório enviado com sucesso",
                    content = @Content(schema = @Schema(implementation = GrimoireResponse.class))),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido ou formato não suportado"),
            @ApiResponse(responseCode = "403", description = "Apenas administradores podem fazer upload"),
            @ApiResponse(responseCode = "404", description = "Nível não encontrado")
    })
    public ResponseEntity<GrimoireResponse> uploadGrimoire(
            @Parameter(description = "ID do nível", required = true) @PathVariable Long levelId,
            @Parameter(description = "Arquivo PDF do grimório", required = true) @RequestParam("file") MultipartFile file,
            @Parameter(description = "Descrição opcional do grimório") @RequestParam(value = "description", required = false) String description) {

        GrimoireResponse grimoire = uploadGrimoireService.execute(levelId, file, description);

        return ResponseEntity.status(HttpStatus.CREATED).body(grimoire);
    }

    @DeleteMapping("/admin/{levelId}")
    @IsAdmin
    @Operation(
            summary = "Deletar grimório (Admin)",
            description = "Remove um grimório do sistema. Apenas administradores podem deletar grimórios."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Grimório deletado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Apenas administradores podem deletar grimórios"),
            @ApiResponse(responseCode = "404", description = "Grimório não encontrado")
    })
    public ResponseEntity<String> deleteGrimoire(
            @Parameter(description = "ID do nível", required = true) @PathVariable Long levelId) {
        deleteGrimoireService.execute(levelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Grimoire deleted successfully");
    }
}
