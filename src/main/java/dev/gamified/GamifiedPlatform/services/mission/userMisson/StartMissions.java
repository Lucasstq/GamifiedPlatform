package dev.gamified.GamifiedPlatform.services.mission.userMisson;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.Mission;
import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserMission;
import dev.gamified.GamifiedPlatform.dtos.response.UserMissionResponse;
import dev.gamified.GamifiedPlatform.enums.MissionStatus;
import dev.gamified.GamifiedPlatform.exceptions.AcessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourseNotFoundException;
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

    /**
     * Inicia uma missão para um usuário específico.
     * Este método coordena todo o fluxo de início de missão, incluindo:
     * - Validação de permissões de acesso
     * - Verificação de existência de usuário e missão
     * - Validação de acesso ao nível da missão
     * - Criação ou atualização do registro UserMission
     * - Validação do status da missão
     *
     * @param userId O ID do usuário que iniciará a missão
     * @param missionId O ID da missão a ser iniciada
     * @return UserMissionResponse com os dados da missão iniciada
     * @throws AcessDeniedException se o usuário não tiver permissão
     * @throws ResourseNotFoundException se usuário ou missão não forem encontrados
     * @throws BusinessException se a missão não puder ser iniciada no estado atual
     * @throws IllegalStateException se o usuário não tiver acesso ao nível da missão
     */
    @Transactional
    public UserMissionResponse execute(Long userId, Long missionId) {
        log.info("User {} started mission {}", userId, missionId);

        validateUserPermission(userId);

        User user = findUser(userId);
        Mission mission = findMission(missionId);

        checkUserAccessToMissionLevel(userId, missionId);

        UserMission userMission = findOrCreateUserMission(user, mission);
        validateMissionStatus(userMission);

        startMission(userMission);

        UserMission savedUserMission = userMissionRepository.save(userMission);
        log.info("Mission {} successfully initiated by user {}", missionId, userId);

        return MissionMapper.toUserMissionResponse(savedUserMission);
    }

    /**
     * Valida se o usuário autenticado tem permissão para iniciar a missão.
     * Verifica se é o dono do recurso ou um administrador.
     *
     * @param userId O ID do usuário a ser validado
     * @throws AcessDeniedException se o usuário não tiver permissão
     */
    private void validateUserPermission(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId)) {
            throw new AcessDeniedException("You do not have permission this feature");
        }
    }

    /**
     * Busca um usuário no banco de dados pelo seu ID.
     *
     * @param userId O ID do usuário a ser buscado
     * @return O objeto User encontrado
     * @throws ResourseNotFoundException se o usuário não for encontrado
     */
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourseNotFoundException("User not found by ID: " + userId));
    }

    /**
     * Busca uma missão no banco de dados pelo seu ID.
     *
     * @param missionId O ID da missão a ser buscada
     * @return O objeto Mission encontrado
     * @throws ResourseNotFoundException se a missão não for encontrada
     */
    private Mission findMission(Long missionId) {
        return missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourseNotFoundException("Mission not found by ID: " + missionId));
    }

    /**
     * Busca ou cria um registro de UserMission para o usuário e missão especificados.
     * Se não existir, cria um novo registro com status AVAILABLE.
     *
     * @param user O usuário que iniciará a missão
     * @param mission A missão a ser iniciada
     * @return O UserMission encontrado ou recém-criado
     */
    private UserMission findOrCreateUserMission(User user, Mission mission) {
        return userMissionRepository.findByUserIdAndMissionId(user.getId(), mission.getId())
                .orElseGet(() -> createNewUserMission(user, mission));
    }

    /**
     * Cria um novo registro de UserMission com status AVAILABLE.
     *
     * @param user O usuário da missão
     * @param mission A missão a ser associada
     * @return O UserMission recém-criado e salvo
     */
    private UserMission createNewUserMission(User user, Mission mission) {
        UserMission newUserMission = UserMission.builder()
                .user(user)
                .mission(mission)
                .status(MissionStatus.AVAILABLE)
                .build();
        return userMissionRepository.save(newUserMission);
    }

    /**
     * Valida se a missão pode ser iniciada baseado no seu status atual.
     * Apenas missões com status AVAILABLE ou FAILED podem ser iniciadas.
     *
     * @param userMission A UserMission a ser validada
     * @throws BusinessException se a missão não puder ser iniciada no estado atual
     */
    private void validateMissionStatus(UserMission userMission) {
        if (userMission.getStatus() != MissionStatus.AVAILABLE &&
                userMission.getStatus() != MissionStatus.FAILED) {
            throw new BusinessException("This mission cannot be started in its current state: " + userMission.getStatus());
        }
    }

    /**
     * Atualiza o status da missão para IN_PROGRESS e registra o horário de início.
     *
     * @param userMission A UserMission a ser iniciada
     */
    private void startMission(UserMission userMission) {
        userMission.setStatus(MissionStatus.IN_PROGRESS);
        userMission.setStartedAt(LocalDateTime.now());
    }

    /**
     * Verifica se o usuário tem acesso ao nível da missão baseado no nível do seu personagem.
     * O nível do personagem deve ser maior ou igual ao nível da missão.
     *
     * @param userId O ID do usuário a ser verificado
     * @param missionId O ID da missão a ser verificada
     * @throws ResourseNotFoundException se o personagem ou missão não forem encontrados
     * @throws IllegalStateException se o usuário não tiver acesso ao nível da missão
     */
    private void checkUserAccessToMissionLevel(Long userId, Long missionId) {
        PlayerCharacter character = playerCharacterRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourseNotFoundException("Character not found for user"));

        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new ResourseNotFoundException("Mission not found by ID: " + missionId));

        if (character.getLevel() < mission.getLevel().getOrderLevel()) {
            throw new IllegalStateException("You don't have access to this level yet.");
        }
    }
}
