package dev.gamified.GamifiedPlatform.services.mission.userMission;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserMission;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserMissionResponse;
import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.MissionMapper;
import dev.gamified.GamifiedPlatform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartMissions {

    private final UserMissionRepository userMissionRepository;
    private final MissionRepository missionRepository;
    private final UserRepository userRepository;
    private final PlayerCharacterRepository playerCharacterRepository;

    /*
     * Inicia uma missão para um usuário específico.
     * Este método coordena todo o fluxo de início de missão, incluindo:
     * - Validação de permissões de acesso
     * - Verificação de existência de usuário e missão
     * - Validação de acesso ao nível da missão
     * - Criação ou atualização do registro UserMission
     * - Validação do status da missão
     */
    @Transactional
    public UserMissionResponse execute(Long missionId) {

        User currentUser = SecurityUtils.getCurrentUserId()
                .flatMap(userRepository::findById)
                .orElseThrow(() -> new AccessDeniedException("User not authenticated"));

        log.info("User {} started mission {}", currentUser.getId(), missionId);

        validateUserPermission(currentUser.getId());

        Mission mission = findMission(missionId);

        checkUserAccessToMissionLevel(currentUser.getId(), missionId);

        UserMission userMission = findOrCreateUserMission(currentUser, mission);
        validateMissionStatus(userMission);

        startMission(userMission);

        UserMission savedUserMission = userMissionRepository.save(userMission);
        log.info("Mission {} successfully initiated by user {}", missionId, currentUser.getId());

        return MissionMapper.toUserMissionResponse(savedUserMission);
    }

    /*
     * Valida se o usuário autenticado tem permissão para iniciar a missão.
     * Verifica se é o dono do recurso ou um administrador.
     */
    private void validateUserPermission(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AccessDeniedException("You do not have permission this feature");
        }
    }

    /*
     * Busca uma missão no banco de dados pelo seu ID.
     */
    private Mission findMission(Long missionId) {
        return missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found by ID: " + missionId));
    }

    /*
     * Busca ou cria um registro de UserMission para o usuário e missão especificados.
     * Se não existir, cria um novo registro com status AVAILABLE.
     */
    private UserMission findOrCreateUserMission(User user, Mission mission) {
        return userMissionRepository.findByUserIdAndMissionId(user.getId(), mission.getId())
                .orElseGet(() -> createNewUserMission(user, mission));
    }

    /*
     * Cria um novo registro de UserMission com status AVAILABLE.
     */
    private UserMission createNewUserMission(User user, Mission mission) {
        UserMission newUserMission = UserMission.builder()
                .user(user)
                .mission(mission)
                .status(MissionStatus.AVAILABLE)
                .build();
        return userMissionRepository.save(newUserMission);
    }

    /*
     * Valida se a missão pode ser iniciada baseado no seu status atual.
     * Apenas missões com status AVAILABLE ou FAILED podem ser iniciadas.
     */
    private void validateMissionStatus(UserMission userMission) {
        if (userMission.getStatus() != MissionStatus.AVAILABLE &&
                userMission.getStatus() != MissionStatus.FAILED) {
            throw new BusinessException("This mission cannot be started in its current state: " + userMission.getStatus());
        }
    }

    /*
     * Atualiza o status da missão para IN_PROGRESS e registra o horário de início.
     */
    private void startMission(UserMission userMission) {
        userMission.setStatus(MissionStatus.IN_PROGRESS);
        userMission.setStartedAt(LocalDateTime.now());
    }

    /*
     * Verifica se o usuário tem acesso ao nível da missão baseado no nível do seu personagem.
     * O nível do personagem deve ser maior ou igual ao nível da missão.
     */
    private void checkUserAccessToMissionLevel(Long userId, Long missionId) {
        PlayerCharacter character = playerCharacterRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Character not found for user"));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourceNotFoundException("Mission not found by ID: " + missionId));

        if (character.getLevel() < mission.getLevel().getOrderLevel()) {
            throw new IllegalStateException("You don't have access to this level yet.");
        }
    }
}
