package dev.gamified.GamifiedPlatform.services.mission.userMisson;

import dev.gamified.GamifiedPlatform.dtos.response.UserMissionResponse;
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
    public Page<UserMissionResponse> execute(Long mentorId, Pageable pageable) {
        log.info("Looking for evaluations made by the mentor. {}", mentorId);
        return userMissionRepository.findAllEvaluatedByMentor(mentorId, pageable)
                .map(MissionMapper::toUserMissionResponse);
    }
}
