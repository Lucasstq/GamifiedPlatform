package dev.gamified.GamifiedPlatform.controller.mission;

import dev.gamified.GamifiedPlatform.config.annotations.*;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionEvaluationRequest;
import dev.gamified.GamifiedPlatform.dtos.request.mission.MissionSubmissionRequest;
import dev.gamified.GamifiedPlatform.dtos.response.missions.MissionProgressResponse;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserMissionResponse;
import dev.gamified.GamifiedPlatform.services.mission.userMisson.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-missions")
@RequiredArgsConstructor
public class UserMissionController {

    private final GetUserMissionsByLevel getUserMissionsByLevel;
    private final StartMissions startMission;
    private final SubmitMission submitMission;
    private final GetMissionsProgress getMissionProgress;
    private final GetPendingEvaluations getPendingEvaluations;
    private final EvaluateMission evaluateMission;
    private final GetEvaluationsByMentor getEvaluationsByMentor;

    @GetMapping("/my-missions/level/{levelId}")
    @CanReadQuests
    public ResponseEntity<List<UserMissionResponse>> getMyMissionsByLevel(@PathVariable Long levelId) {
        return ResponseEntity.ok(getUserMissionsByLevel.execute(levelId));
    }

    @PostMapping("/{missionId}/start")
    @CanInitiateQuests
    public ResponseEntity<UserMissionResponse> startMission(@PathVariable Long missionId) {
        return ResponseEntity.ok(startMission.execute(missionId));
    }

    @PostMapping("/{missionId}/submit")
    @CanCompleteQuests
    public ResponseEntity<UserMissionResponse> submitMission(@PathVariable Long missionId,
                                                             @Valid @RequestBody MissionSubmissionRequest request) {
        return ResponseEntity.ok(submitMission.execute(missionId, request));
    }

    @GetMapping("/my-progress/level/{levelId}")
    @CanReadProfile
    public ResponseEntity<MissionProgressResponse> getMyProgress(@PathVariable Long levelId) {
        return ResponseEntity.ok(getMissionProgress.execute(levelId));
    }

    @GetMapping("/pending")
    @CanReadPedingQuest
    public ResponseEntity<Page<UserMissionResponse>> getPendingEvaluations(Pageable pageable) {
        return ResponseEntity.ok(getPendingEvaluations.execute(pageable));
    }

    @PostMapping("/{userMissionId}/evaluate")
    @CanQuestsEvaluate
    public ResponseEntity<UserMissionResponse> evaluateMission(
            @PathVariable Long userMissionId,
            @Valid @RequestBody MissionEvaluationRequest request) {
        return ResponseEntity.ok(evaluateMission.execute(userMissionId, request));
    }

    @GetMapping("/my-evaluations")
    @CanQuestsEvaluate
    public ResponseEntity<Page<UserMissionResponse>> getMyEvaluations(Pageable pageable) {
        return ResponseEntity.ok(getEvaluationsByMentor.execute(pageable));
    }
}

