package dev.gamified.GamifiedPlatform.services.mission.userMisson;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.dtos.response.UserMissionResponse;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetEvaluationsByMentor {

    private final UserMissionRepository userMissionRepository;

    @Transactional(readOnly = true)
    public Page<UserMissionResponse> execute(Pageable pageable) {

        Long mentorId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new AccessDeniedException("User not authenticated"));

        // Admin vê TODAS as avaliações do sistema
        if (SecurityUtils.hasAdminRole()) {
            log.info("Admin {} requesting all evaluations from the system", mentorId);
            return userMissionRepository.findAllEvaluations(pageable)
                    .map(MissionMapper::toUserMissionResponse);
        }

        // Mentor vê apenas suas próprias avaliações
        log.info("Mentor {} requesting their own evaluations", mentorId);
        return userMissionRepository.findAllEvaluatedByMentor(mentorId, pageable)
                .map(MissionMapper::toUserMissionResponse);
    }
}
