package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.config.security.SecurityUtils;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserBadgeResponse;
import dev.gamified.GamifiedPlatform.enums.Roles;
import dev.gamified.GamifiedPlatform.exceptions.AccessDeniedException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.BadgeMapper;
import dev.gamified.GamifiedPlatform.repository.UserBadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * Busca todos os badges de um usuário paginados.
     * Requer permissão: próprio usuário, admin ou mentor.
     */
    @Transactional(readOnly = true)
    public Page<UserBadgeResponse> execute(Long userId, Pageable pageable) {

        log.info("Fetching badges for user {} - page: {}, size: {}",
                userId, pageable.getPageNumber(), pageable.getPageSize());

        validateUserPermission(userId);
        validateUserExists(userId);

        List<UserBadgeResponse> allBadges = userBadgeRepository.findAllByUserId(userId).stream()
                .map(BadgeMapper::toUserBadgeResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allBadges.size());

        List<UserBadgeResponse> pageContent = allBadges.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allBadges.size());
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

