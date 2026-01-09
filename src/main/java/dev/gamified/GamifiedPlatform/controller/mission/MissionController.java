package dev.gamified.GamifiedPlatform.controller.mission;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadQuests;
import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionCreateRequest;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionUpdateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.missions.MissionResponse;
import dev.gamified.GamifiedPlatform.services.mission.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/missions")
@RequiredArgsConstructor
@Tag(name = "Missões", description = "Desafios de programação e sistema de XP")
@SecurityRequirement(name = "bearerAuth")
public class MissionController {

    private final GetAllMissionsService getAllMissions;
    private final GetMissionByLevelService getMissionsByLevel;
    private final GetMissionById getMissionById;
    private final CreateMissionService createMission;
    private final UpdateMissionService updateMission;
    private final DeleteMissionService deleteMission;

    @GetMapping
    @CanReadQuests
    @Operation(
            summary = "Listar todas as missões",
            description = "Retorna uma lista paginada de todas as missões disponíveis no sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de missões retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<MissionResponse>> getAllMissions(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(getAllMissions.execute(pageable));
    }

    @GetMapping("/level/{levelId}")
    @CanReadQuests
    @Operation(
            summary = "Listar missões por nível",
            description = "Retorna uma lista paginada de missões específicas de um nível"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de missões retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "404", description = "Nível não encontrado")
    })
    public ResponseEntity<Page<MissionResponse>> getMissionsByLevel(
            @Parameter(description = "ID do nível", required = true) @PathVariable Long levelId,
            @PageableDefault(size = 20, sort = "orderNumber") Pageable pageable) {
        return ResponseEntity.ok(getMissionsByLevel.execute(levelId, pageable));
    }

    @GetMapping("/{missionId}")
    @CanReadQuests
    @Operation(
            summary = "Buscar missão por ID",
            description = "Retorna os detalhes de uma missão específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Missão encontrada",
                    content = @Content(schema = @Schema(implementation = MissionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Missão não encontrada")
    })
    public ResponseEntity<MissionResponse> getMissionById(
            @Parameter(description = "ID da missão", required = true) @PathVariable Long missionId) {
        return ResponseEntity.ok(getMissionById.execute(missionId));
    }

    @PostMapping
    @IsAdmin
    @Operation(
            summary = "Criar nova missão",
            description = "Cria uma nova missão no sistema. Apenas administradores podem criar missões."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Missão criada com sucesso",
                    content = @Content(schema = @Schema(implementation = MissionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Apenas administradores podem criar missões")
    })
    public ResponseEntity<MissionResponse> createMission(@Valid @RequestBody MissionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createMission.execute(request));
    }

    @PutMapping("/{missionId}")
    @IsAdmin
    @Operation(
            summary = "Atualizar missão",
            description = "Atualiza os dados de uma missão existente. Apenas administradores podem atualizar missões."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Missão atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = MissionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "403", description = "Apenas administradores podem atualizar missões"),
            @ApiResponse(responseCode = "404", description = "Missão não encontrada")
    })
    public ResponseEntity<MissionResponse> updateMission(
            @Parameter(description = "ID da missão", required = true) @PathVariable Long missionId,
            @Valid @RequestBody MissionUpdateRequest request) {
        return ResponseEntity.ok(updateMission.execute(missionId, request));
    }

    @DeleteMapping("/{missionId}")
    @IsAdmin
    @Operation(
            summary = "Deletar missão",
            description = "Remove uma missão do sistema. Apenas administradores podem deletar missões."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Missão deletada com sucesso"),
            @ApiResponse(responseCode = "403", description = "Apenas administradores podem deletar missões"),
            @ApiResponse(responseCode = "404", description = "Missão não encontrada")
    })
    public ResponseEntity<Void> deleteMission(
            @Parameter(description = "ID da missão", required = true) @PathVariable Long missionId) {
        deleteMission.execute(missionId);
        return ResponseEntity.noContent().build();
    }
}

