package dev.gamified.GamifiedPlatform.controller.mission;

import dev.gamified.GamifiedPlatform.config.annotations.CanReadQuests;
import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionCreateRequest;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionUpdateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.missions.MissionResponse;
import dev.gamified.GamifiedPlatform.services.mission.*;
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
public class MissionController {

    private final GetAllMissionsService getAllMissions;
    private final GetMissionByLevelService getMissionsByLevel;
    private final GetMissionById getMissionById;
    private final CreateMissionService createMission;
    private final UpdateMissonService updateMission;
    private final DeleteMissionService deleteMission;

    @GetMapping
    @CanReadQuests
    public ResponseEntity<Page<MissionResponse>> getAllMissions(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(getAllMissions.execute(pageable));
    }

    @GetMapping("/level/{levelId}")
    @CanReadQuests
    public ResponseEntity<Page<MissionResponse>> getMissionsByLevel(
            @PathVariable Long levelId,
            @PageableDefault(size = 20, sort = "orderNumber") Pageable pageable) {
        return ResponseEntity.ok(getMissionsByLevel.execute(levelId, pageable));
    }

    @GetMapping("/{missionId}")
    @CanReadQuests
    public ResponseEntity<MissionResponse> getMissionById(@PathVariable Long missionId) {
        return ResponseEntity.ok(getMissionById.execute(missionId));
    }

    @PostMapping
    @IsAdmin
    public ResponseEntity<MissionResponse> createMission(@Valid @RequestBody MissionCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createMission.execute(request));
    }

    @PutMapping("/{missionId}")
    @IsAdmin
    public ResponseEntity<MissionResponse> updateMission(
            @PathVariable Long missionId,
            @Valid @RequestBody MissionUpdateRequest request) {
        return ResponseEntity.ok(updateMission.execute(missionId, request));
    }

    @DeleteMapping("/{missionId}")
    @IsAdmin
    public ResponseEntity<Void> deleteMission(@PathVariable Long missionId) {
        deleteMission.execute(missionId);
        return ResponseEntity.noContent().build();
    }
}

