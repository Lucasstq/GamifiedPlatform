package dev.gamified.GamifiedPlatform.controller.level;

import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.request.level.LevelRequest;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.services.levels.CreateLevelService;
import dev.gamified.GamifiedPlatform.services.levels.DeleteLevelService;
import dev.gamified.GamifiedPlatform.services.levels.UpdateLevelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 * Controller responsável pelos endpoints administrativos relacionados aos níveis de gamificação.
 */
@RestController
@RequestMapping("/levels/admin")
@RequiredArgsConstructor
public class LevelAdminController {

    private final CreateLevelService createLevel;
    private final UpdateLevelService updateLevel;
    private final DeleteLevelService deleteLevel;

    /**
     * POST /levels - Criar novo nível (apenas ADMIN)
     */
    @PostMapping
    @IsAdmin
    public ResponseEntity<LevelResponse> createLevel(@Valid @RequestBody LevelRequest request) {
        LevelResponse response = createLevel.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /levels/{id} - Atualizar nível (apenas ADMIN)
     */
    @PutMapping("/{id}")
    @IsAdmin
    public ResponseEntity<LevelResponse> updateLevel(@PathVariable Long id, @Valid @RequestBody LevelRequest request) {
        LevelResponse updatedLevel = updateLevel.execute(id, request);
        return ResponseEntity.ok(updatedLevel);
    }

    /**
     * DELETE /levels/{id} - Deletar nível (apenas ADMIN)
     */
    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<Void> deleteLevel(@PathVariable Long id) {
        deleteLevel.execute(id);
        return ResponseEntity.noContent().build();
    }
}
