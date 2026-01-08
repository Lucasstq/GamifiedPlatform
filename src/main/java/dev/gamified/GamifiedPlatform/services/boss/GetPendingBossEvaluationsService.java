package dev.gamified.GamifiedPlatform.services.boss;

import dev.gamified.GamifiedPlatform.domain.UserBoss;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.UserBossResponse;
import dev.gamified.GamifiedPlatform.mapper.BossMapper;
import dev.gamified.GamifiedPlatform.repository.UserBossRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável por listar boss fights pendentes de avaliação.
 * Apenas mentores e admins podem usar este serviço.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetPendingBossEvaluationsService {

    private final UserBossRepository userBossRepository;

    /*
     * Retorna boss fights aguardando avaliação.
     */
    @Transactional(readOnly = true)
    public Page<UserBossResponse> execute(Pageable pageable) {
        log.info("Getting pending boss evaluations");

        Page<UserBoss> pendingBosses = userBossRepository.findPendingEvaluations(pageable);

        return pendingBosses.map(BossMapper::toUserBossResponse);
    }
}

