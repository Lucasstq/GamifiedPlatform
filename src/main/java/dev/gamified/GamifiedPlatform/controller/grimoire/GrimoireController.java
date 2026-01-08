package dev.gamified.GamifiedPlatform.controller.grimoire;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadUsers;
import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.response.grimoire.GrimoireResponse;
import dev.gamified.GamifiedPlatform.services.grimoire.*;
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

/*
 * Controller para gerenciar grimórios (PDFs educacionais).
 * Grimórios são desbloqueados ao atingir determinado nível.
 */
@RestController
@RequestMapping("/grimoires")
@RequiredArgsConstructor
public class GrimoireController {

    private final GetAllGrimoiresService getAllGrimoiresService;
    private final GetGrimoireInfoService getGrimoireInfoService;
    private final DownloadGrimoireService downloadGrimoireService;
    private final UploadGrimoireService uploadGrimoireService;
    private final DeleteGrimoireService deleteGrimoireService;

    /*
     * Lista todos os grimórios disponíveis paginados.
     * Indica quais o usuário pode acessar baseado no seu nível.
     */
    @GetMapping
    @CanReadUsers
    public ResponseEntity<Page<GrimoireResponse>> listGrimoires(
            @PageableDefault(size = 20, sort = "uploadedAt") Pageable pageable) {
        return ResponseEntity.ok(getAllGrimoiresService.execute(pageable));
    }

    /*
     * Busca informações de um grimório específico.
     */
    @GetMapping("/{levelId}")
    @CanReadUsers
    public ResponseEntity<GrimoireResponse> getGrimoireInfo(@PathVariable Long levelId) {
        return ResponseEntity.ok(getGrimoireInfoService.execute(levelId));
    }

    /*
     * Faz download de um grimório (se desbloqueado).
     * Valida se o usuário tem nível suficiente.
     */
    @GetMapping("/{levelId}/download")
    @CanReadUsers
    public ResponseEntity<InputStreamResource> downloadGrimoire(@PathVariable Long levelId) {

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

    /*
     * Upload de um grimório PDF (ADMIN).
     */
    @PostMapping("/admin/{levelId}")
    @IsAdmin
    public ResponseEntity<GrimoireResponse> uploadGrimoire(
            @PathVariable Long levelId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {

        GrimoireResponse grimoire = uploadGrimoireService.execute(levelId, file, description);

        return ResponseEntity.status(HttpStatus.CREATED).body(grimoire);
    }

    /*
     * Deleta um grimório (ADMIN).
     */
    @DeleteMapping("/admin/{levelId}")
    @IsAdmin
    public ResponseEntity<String> deleteGrimoire(@PathVariable Long levelId) {
        deleteGrimoireService.execute(levelId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Grimoire deleted successfully");
    }
}
