package dev.gamified.GamifiedPlatform.services.admin;

import dev.gamified.GamifiedPlatform.domain.Boss;
import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.dtos.response.admin.AdminDashboardResponse;
import dev.gamified.GamifiedPlatform.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetAdminDashboardService Tests")
class GetAdminDashboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MissionRepository missionRepository;

    @Mock
    private BossRepository bossRepository;

    @Mock
    private LevelRepository levelRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private UserMissionRepository userMissionRepository;

    @Mock
    private UserBossRepository userBossRepository;

    @InjectMocks
    private GetAdminDashboardService getAdminDashboardService;

    private Mission mission;
    private Boss boss;

    @BeforeEach
    void setUp() {
        mission = Mission.builder()
                .id(1L)
                .title("Test Mission")
                .build();

        boss = Boss.builder()
                .id(1L)
                .name("Dragon Boss")
                .build();
    }

    @Test
    @DisplayName("Deve retornar dashboard com todas as estatísticas")
    void shouldReturnDashboardWithAllStatistics() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countActiveUsers()).thenReturn(80L);
        when(missionRepository.count()).thenReturn(50L);
        when(bossRepository.count()).thenReturn(10L);
        when(levelRepository.count()).thenReturn(20L);
        when(badgeRepository.count()).thenReturn(30L);
        when(missionRepository.findAll()).thenReturn(Collections.emptyList());
        when(bossRepository.findAll()).thenReturn(Collections.emptyList());

        AdminDashboardResponse response = getAdminDashboardService.execute();

        assertNotNull(response);
        assertEquals(100L, response.totalUsers());
        assertEquals(80L, response.activeUsers());
        assertEquals(50L, response.totalMissions());
        assertEquals(10L, response.totalBosses());
        assertEquals(20L, response.totalLevels());
        assertEquals(30L, response.totalBadges());
    }

    @Test
    @DisplayName("Deve retornar taxa de completude zero quando não há missões")
    void shouldReturnZeroCompletionRateWhenNoMissions() {
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countActiveUsers()).thenReturn(5L);
        when(missionRepository.count()).thenReturn(0L);
        when(bossRepository.count()).thenReturn(0L);
        when(levelRepository.count()).thenReturn(0L);
        when(badgeRepository.count()).thenReturn(0L);
        when(missionRepository.findAll()).thenReturn(Collections.emptyList());
        when(bossRepository.findAll()).thenReturn(Collections.emptyList());

        AdminDashboardResponse response = getAdminDashboardService.execute();

        assertNotNull(response);
        assertEquals(0.0, response.averageCompletionRate());
    }

    @Test
    @DisplayName("Deve calcular taxa média de completude corretamente")
    void shouldCalculateAverageCompletionRateCorrectly() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countActiveUsers()).thenReturn(80L);
        when(missionRepository.count()).thenReturn(2L);
        when(bossRepository.count()).thenReturn(0L);
        when(levelRepository.count()).thenReturn(0L);
        when(badgeRepository.count()).thenReturn(0L);

        Mission mission2 = Mission.builder().id(2L).title("Mission 2").build();
        when(missionRepository.findAll()).thenReturn(List.of(mission, mission2));
        when(bossRepository.findAll()).thenReturn(Collections.emptyList());

        // Missão 1: 10 tentativas, 8 completadas = 80%
        when(userMissionRepository.countByMissionId(1L)).thenReturn(10L);
        when(userMissionRepository.countCompletedByMissionId(1L)).thenReturn(8L);
        when(userMissionRepository.countFailedByMissionId(1L)).thenReturn(2L);

        // Missão 2: 20 tentativas, 10 completadas = 50%
        when(userMissionRepository.countByMissionId(2L)).thenReturn(20L);
        when(userMissionRepository.countCompletedByMissionId(2L)).thenReturn(10L);
        when(userMissionRepository.countFailedByMissionId(2L)).thenReturn(10L);

        AdminDashboardResponse response = getAdminDashboardService.execute();

        assertNotNull(response);
        // Média: (80 + 50) / 2 = 65%
        assertEquals(65.0, response.averageCompletionRate());
    }

    @Test
    @DisplayName("Deve identificar a missão mais difícil corretamente")
    void shouldIdentifyHardestMissionCorrectly() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countActiveUsers()).thenReturn(80L);
        when(missionRepository.count()).thenReturn(2L);
        when(bossRepository.count()).thenReturn(0L);
        when(levelRepository.count()).thenReturn(0L);
        when(badgeRepository.count()).thenReturn(0L);

        Mission hardMission = Mission.builder().id(2L).title("Hard Mission").build();
        when(missionRepository.findAll()).thenReturn(List.of(mission, hardMission));
        when(bossRepository.findAll()).thenReturn(Collections.emptyList());

        // Missão fácil: 10 tentativas, 2 falhas = 20% taxa de falha
        when(userMissionRepository.countByMissionId(1L)).thenReturn(10L);
        when(userMissionRepository.countCompletedByMissionId(1L)).thenReturn(8L);
        when(userMissionRepository.countFailedByMissionId(1L)).thenReturn(2L);

        // Missão difícil: 20 tentativas, 15 falhas = 75% taxa de falha
        when(userMissionRepository.countByMissionId(2L)).thenReturn(20L);
        when(userMissionRepository.countCompletedByMissionId(2L)).thenReturn(5L);
        when(userMissionRepository.countFailedByMissionId(2L)).thenReturn(15L);

        AdminDashboardResponse response = getAdminDashboardService.execute();

        assertNotNull(response);
        assertNotNull(response.missionDifficultyStats());
        assertEquals(2L, response.missionDifficultyStats().missionId());
        assertEquals("Hard Mission", response.missionDifficultyStats().missionTitle());
        assertEquals(75.0, response.missionDifficultyStats().failureRate());
    }

    @Test
    @DisplayName("Deve retornar null para missão mais difícil quando não há missões")
    void shouldReturnNullForHardestMissionWhenNoMissions() {
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countActiveUsers()).thenReturn(5L);
        when(missionRepository.count()).thenReturn(0L);
        when(bossRepository.count()).thenReturn(0L);
        when(levelRepository.count()).thenReturn(0L);
        when(badgeRepository.count()).thenReturn(0L);
        when(missionRepository.findAll()).thenReturn(Collections.emptyList());
        when(bossRepository.findAll()).thenReturn(Collections.emptyList());

        AdminDashboardResponse response = getAdminDashboardService.execute();

        assertNotNull(response);
        assertNull(response.missionDifficultyStats());
    }

    @Test
    @DisplayName("Deve identificar o boss mais invicto corretamente")
    void shouldIdentifyMostUndefeatedBossCorrectly() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countActiveUsers()).thenReturn(80L);
        when(missionRepository.count()).thenReturn(0L);
        when(bossRepository.count()).thenReturn(2L);
        when(levelRepository.count()).thenReturn(0L);
        when(badgeRepository.count()).thenReturn(0L);
        when(missionRepository.findAll()).thenReturn(Collections.emptyList());

        Boss strongBoss = Boss.builder().id(2L).name("Invincible Dragon").build();
        when(bossRepository.findAll()).thenReturn(List.of(boss, strongBoss));

        // Boss 1: 20 tentativas, 15 derrotas = 75% taxa de derrota
        when(userBossRepository.countByBossId(1L)).thenReturn(20L);
        when(userBossRepository.countDefeatedByBossId(1L)).thenReturn(15L);

        // Boss 2: 30 tentativas, 5 derrotas = 16.67% taxa de derrota (mais invicto)
        when(userBossRepository.countByBossId(2L)).thenReturn(30L);
        when(userBossRepository.countDefeatedByBossId(2L)).thenReturn(5L);

        AdminDashboardResponse response = getAdminDashboardService.execute();

        assertNotNull(response);
        assertNotNull(response.bossDefeatedStats());
        assertEquals(2L, response.bossDefeatedStats().bossId());
        assertEquals("Invincible Dragon", response.bossDefeatedStats().bossName());
    }

    @Test
    @DisplayName("Deve retornar null para boss quando não há bosses")
    void shouldReturnNullForBossWhenNoBosses() {
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countActiveUsers()).thenReturn(5L);
        when(missionRepository.count()).thenReturn(0L);
        when(bossRepository.count()).thenReturn(0L);
        when(levelRepository.count()).thenReturn(0L);
        when(badgeRepository.count()).thenReturn(0L);
        when(missionRepository.findAll()).thenReturn(Collections.emptyList());
        when(bossRepository.findAll()).thenReturn(Collections.emptyList());

        AdminDashboardResponse response = getAdminDashboardService.execute();

        assertNotNull(response);
        assertNull(response.bossDefeatedStats());
    }
}

