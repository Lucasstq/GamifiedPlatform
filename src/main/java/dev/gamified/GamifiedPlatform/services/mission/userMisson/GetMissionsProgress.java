package dev.gamified.GamifiedPlatform.services.mission.userMisson;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.response.missions.MissionProgressResponse;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetMissionsProgress {

    private final UserMissionRepository userMissionRepository;
    private final MissionRepository missionRepository;
    private final LevelRepository levelRepository;

    /*
     * Calcula e retorna o progresso de missões de um usuário em um nível específico.
     * Este método coordena todo o fluxo de cálculo do progresso, incluindo:
     * - Busca do nível
     * - Contagem de missões totais e completadas
     * - Cálculo de porcentagem de progresso
     * - Verificação se pode desbloquear o boss
     */
    @Transactional(readOnly = true)
    public MissionProgressResponse execute(Long levelId) {

        Long userId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new ResourceNotFoundException("User not authenticated"));

        log.info("Calculating user mission progress {} at level {}", userId, levelId);

        validateUserPermission(userId);

        Levels level = findLevel(levelId);
        Long totalMissions = getTotalMissions(levelId);
        Long completedMissions = getCompletedMissions(userId, levelId);

        double progressPercentage = calculateProgressPercentage(totalMissions, completedMissions);
        boolean canUnlockBoss = canUnlockBoss(progressPercentage);

        return buildMissionProgressResponse(level, totalMissions, completedMissions, progressPercentage, canUnlockBoss);
    }

    private void validateUserPermission(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AccessDeniedException("You do not have permission this feature");
        }
    }

    /*
     * Busca um nível no banco de dados pelo seu ID.
     * Lança exceção se o nível não for encontrado.
     */
    private Levels findLevel(Long levelId) {
        return levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found"));
    }

    /*
     * Conta o número total de missões disponíveis em um determinado nível.
     */
    private Long getTotalMissions(Long levelId) {
        return missionRepository.countByLevelId(levelId);
    }

    /*
     * Conta quantas missões foram completadas por um usuário específico em um nível.
     * Apenas missões com status COMPLETED são contabilizadas.
     */
    private Long getCompletedMissions(Long userId, Long levelId) {
        return userMissionRepository.countCompletedMissionsByUserAndLevel(userId, levelId);
    }

    /*
     * Calcula a porcentagem de progresso das missões completadas em relação ao total.
     * Retorna 0.0 se não houver missões no nível para evitar divisão por zero.
     */
    private double calculateProgressPercentage(Long totalMissions, Long completedMissions) {
        if (totalMissions == 0) {
            return 0.0;
        }
        return (completedMissions.doubleValue() / totalMissions.doubleValue()) * 100;
    }

    /*
     * Verifica se o usuário atingiu progresso suficiente para desbloquear o boss do nível.
     * O critério é ter completado 80% ou mais das missões do nível.
     */
    private boolean canUnlockBoss(double progressPercentage) {
        return progressPercentage >= 80.0;
    }

    /*
     * Constrói o objeto de resposta com todas as informações de progresso das missões.
     * A porcentagem é arredondada para 2 casas decimais.
     */
    private MissionProgressResponse buildMissionProgressResponse(
            Levels level,
            Long totalMissions,
            Long completedMissions,
            double progressPercentage,
            boolean canUnlockBoss) {

        return MissionProgressResponse.builder()
                .levelId(level.getId())
                .levelName(level.getName())
                .totalMissions(totalMissions)
                .completedMissions(completedMissions)
                .progressPercentage(Math.round(progressPercentage * 100.0) / 100.0)
                .canUnlockBoss(canUnlockBoss)
                .build();
    }
}
