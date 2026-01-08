package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.domain.UserBadge;
import dev.gamified.GamifiedPlatform.dtos.response.UserBadgeResponse;
import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.BadgeMapper;
import dev.gamified.GamifiedPlatform.repository.UserBadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Serviço para buscar todos os badges conquistados por um usuário.
 * Valida permissões antes de retornar os dados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetUserBadgesService {

    private final UserBadgeRepository userBadgeRepository;
    private final UserRepository userRepository;

    /*
     * Busca todos os badges de um usuário.
     * Requer permissão: próprio usuário, admin ou mentor.
     */
    @Transactional(readOnly = true)
    public List<UserBadgeResponse> execute(Long userId) {

        log.info("Fetching badges for user {}", userId);

        validateUserPermission(userId);
        validateUserExists(userId);

        List<UserBadge> userBadges = userBadgeRepository.findAllByUserId(userId);

        return userBadges.stream()
                .map(BadgeMapper::toUserBadgeResponse)
                .collect(Collectors.toList());
    }

    /*
     * Verifica se o usuário autenticado tem permissão para ver os badges.
     * Permite acesso se for o próprio usuário, admin ou mentor.
     */
    private void validateUserPermission(Long userId) {
        if (!SecurityUtils.isResourceOwnerOrAdmin(userId) && !SecurityUtils.hasRole(Roles.ROLE_MENTOR)) {
            throw new AccessDeniedException("You do not have permission to view this user's badges");
        }
    }

    /*
     * Valida se o usuário existe no sistema.
     */
    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
    }
}

