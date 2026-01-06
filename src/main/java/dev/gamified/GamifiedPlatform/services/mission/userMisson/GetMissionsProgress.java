package dev.gamified.GamifiedPlatform.services.mission.userMisson;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.response.MissionProgressResponse;
import dev.gamified.GamifiedPlatform.exceptions.AcessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
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

    /**
     * Calcula e retorna o progresso de missões de um usuário em um nível específico.
     * Este método coordena todo o fluxo de cálculo do progresso, incluindo:
     * - Busca do nível
     * - Contagem de missões totais e completadas
     * - Cálculo de porcentagem de progresso
     * - Verificação se pode desbloquear o boss
     *
     * @param userId  O ID do usuário para verificar o progresso
     * @param levelId O ID do nível a ser analisado
     * @return MissionProgressResponse contendo todas as informações de progresso
     */
    @Transactional(readOnly = true)
    public MissionProgressResponse execute(Long userId, Long levelId) {
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
            throw new AcessDeniedException("You do not have permission this feature");
        }
    }

    /**
     * Busca um nível no banco de dados pelo seu ID.
     * Lança exceção se o nível não for encontrado.
     *
     * @param levelId O ID do nível a ser buscado
     * @return O objeto Levels encontrado
     * @throws ResourseNotFoundException se o nível não existir
     */
    private Levels findLevel(Long levelId) {
        return levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourseNotFoundException("Level not found"));
    }

    /**
     * Conta o número total de missões disponíveis em um determinado nível.
     *
     * @param levelId O ID do nível para contar as missões
     * @return O número total de missões no nível
     */
    private Long getTotalMissions(Long levelId) {
        return missionRepository.countByLevelId(levelId);
    }

    /**
     * Conta quantas missões foram completadas por um usuário específico em um nível.
     * Apenas missões com status COMPLETED são contabilizadas.
     *
     * @param userId  O ID do usuário
     * @param levelId O ID do nível
     * @return O número de missões completadas pelo usuário no nível
     */
    private Long getCompletedMissions(Long userId, Long levelId) {
        return userMissionRepository.countCompletedMissionsByUserAndLevel(userId, levelId);
    }

    /**
     * Calcula a porcentagem de progresso das missões completadas em relação ao total.
     * Retorna 0.0 se não houver missões no nível para evitar divisão por zero.
     *
     * @param totalMissions     O número total de missões no nível
     * @param completedMissions O número de missões completadas
     * @return A porcentagem de progresso (0.0 a 100.0)
     */
    private double calculateProgressPercentage(Long totalMissions, Long completedMissions) {
        if (totalMissions == 0) {
            return 0.0;
        }
        return (completedMissions.doubleValue() / totalMissions.doubleValue()) * 100;
    }

    /**
     * Verifica se o usuário atingiu progresso suficiente para desbloquear o boss do nível.
     * O critério é ter completado 80% ou mais das missões do nível.
     *
     * @param progressPercentage A porcentagem de progresso atual
     * @return true se pode desbloquear o boss (>= 80%), false caso contrário
     */
    private boolean canUnlockBoss(double progressPercentage) {
        return progressPercentage >= 80.0;
    }

    /**
     * Constrói o objeto de resposta com todas as informações de progresso das missões.
     * A porcentagem é arredondada para 2 casas decimais.
     *
     * @param level              O objeto do nível
     * @param totalMissions      O número total de missões
     * @param completedMissions  O número de missões completadas
     * @param progressPercentage A porcentagem de progresso calculada
     * @param canUnlockBoss      Se pode desbloquear o boss
     * @return MissionProgressResponse com todos os dados formatados
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
