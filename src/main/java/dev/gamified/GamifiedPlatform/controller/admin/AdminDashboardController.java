package dev.gamified.GamifiedPlatform.controller.admin;

import dev.gamified.GamifiedPlatform.config.annotations.IsAdmin;
import dev.gamified.GamifiedPlatform.dtos.response.admin.AdminDashboardResponse;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.BossStatsResponse;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelCompletionStatsResponse;
import dev.gamified.GamifiedPlatform.dtos.response.missions.MissionDifficultyAnalysisResponse;
import dev.gamified.GamifiedPlatform.services.admin.GetAdminDashboardService;
import dev.gamified.GamifiedPlatform.services.admin.GetBossStatsService;
import dev.gamified.GamifiedPlatform.services.admin.GetLevelCompletionStatsService;
import dev.gamified.GamifiedPlatform.services.admin.GetMissionDifficultyAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final GetAdminDashboardService getAdminDashboardService;
    private final GetMissionDifficultyAnalysisService getMissionDifficultyAnalysisService;
    private final GetBossStatsService getBossStatsService;
    private final GetLevelCompletionStatsService getLevelCompletionStatsService;

    @GetMapping
    @IsAdmin
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        return ResponseEntity.ok(getAdminDashboardService.execute());
    }

    @GetMapping("/missions/difficulty")
    @IsAdmin
    public ResponseEntity<MissionDifficultyAnalysisResponse> getMissionDifficulty() {
        return ResponseEntity.ok(getMissionDifficultyAnalysisService.execute());
    }

    @GetMapping("/bosses/stats")
    @IsAdmin
    public ResponseEntity<BossStatsResponse> getBossStats() {
        return ResponseEntity.ok(getBossStatsService.execute());
    }

    @GetMapping("/levels/completion")
    @IsAdmin
    public ResponseEntity<List<LevelCompletionStatsResponse>> getLevelCompletionStats() {
        return ResponseEntity.ok(getLevelCompletionStatsService.execute());
    }
}

