package dev.gamified.GamifiedPlatform.services.mission.userMisson;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserMission;
import dev.gamified.GamifiedPlatform.dtos.response.UserMissionResponse;
import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import dev.gamified.GamifiedPlatform.exceptions.AcessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import dev.gamified.GamifiedPlatform.repository.MissionRepository;
import dev.gamified.GamifiedPlatform.repository.UserMissionRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserMissionsByLevel {

    private final UserMissionRepository userMissionRepository;
    private final UserRepository userRepository;
    private final MissionRepository missionRepository;
    private final LevelRepository levelRepository;

    @Transactional(readOnly = true)
    public List<UserMissionResponse> execute(Long userId, Long levelId) {
        log.info("Searching for user missions at level {}", levelId);

        isOwnerOrAdmin(userId);
        findResources(userId, levelId);

        List<UserMission> userMissions = findUserMissions(userId, levelId);

        return userMissions.stream()
                .map(MissionMapper::toUserMissionResponse)
                .collect(Collectors.toList());
    }

    // Verifica se o usuário autenticado é o dono do recurso ou um admin
    private void isOwnerOrAdmin(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AcessDeniedException("You do not have permission to update this user");
        }
    }

    private void findResources(Long userId, Long levelId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourseNotFoundException("User not found for ID: " + userId));

        levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourseNotFoundException("Level not found for ID: " + levelId));
    }

    private List<UserMission> findUserMissions(Long userId, Long levelId) {
        List<UserMission> byUserIdAndLevelId = userMissionRepository.findByUserIdAndLevelId(userId, levelId);

        // Se não existir UserMission, criar automaticamente para todas as missões do nível
        if (byUserIdAndLevelId.isEmpty()) {
            List<Mission> missions = missionRepository.findByLevelIdOrderByOrderNumberAsc(levelId);
            User user = userRepository.findById(userId).orElseThrow();
            byUserIdAndLevelId = missions.stream()
                    .map(mission -> {
                        UserMission userMission = UserMission.builder()
                                .user(user)
                                .mission(mission)
                                .status(MissionStatus.AVAILABLE)
                                .build();
                        return userMissionRepository.save(userMission);
                    })
                    .toList();

        }
        return byUserIdAndLevelId;
    }

}
