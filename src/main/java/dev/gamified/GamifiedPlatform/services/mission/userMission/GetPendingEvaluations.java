package dev.gamified.GamifiedPlatform.services.mission.userMission;

import dev.gamified.GamifiedPlatform.dtos.response.user.UserMissionResponse;
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
public class GetPendingEvaluations {

    private final UserMissionRepository userMissionRepository;

    @Transactional(readOnly = true)
    public Page<UserMissionResponse> execute(Pageable pageable) {
        log.info("Looking for pending missions to be evaluated.");
        return userMissionRepository.findAllPendingEvaluations(pageable)
                .map(MissionMapper::toUserMissionResponse);
    }
}
