package dev.gamified.GamifiedPlatform.services.boss;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.UserBoss;
import dev.gamified.GamifiedPlatform.dtos.response.UserBossResponse;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.mapper.BossMapper;
import dev.gamified.GamifiedPlatform.repository.UserBossRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Serviço responsável por listar as avaliações de boss fights feitas por um mentor.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetMyBossEvaluationsService {

    private final UserBossRepository userBossRepository;

    /*
     * Retorna boss fights avaliadas por um mentor específico.
     */
    @Transactional(readOnly = true)
    public Page<UserBossResponse> execute(Pageable pageable) {

        Long mentorId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User not authenticated"));

        log.info("Getting boss evaluations made by mentor {}", mentorId);

        checkPermission(mentorId);
        Page<UserBoss> evaluatedBosses = userBossRepository.findByEvaluatedById(mentorId, pageable);

        return evaluatedBosses.map(BossMapper::toUserBossResponse);
    }

    private void checkPermission(Long mentorId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(mentorId)) {
            throw new AccessDeniedException("You do not have permission to access these evaluations.");
        }
    }
}

