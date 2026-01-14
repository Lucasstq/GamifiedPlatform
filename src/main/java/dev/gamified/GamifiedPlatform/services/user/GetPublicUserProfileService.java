package dev.gamified.GamifiedPlatform.services.user;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.dtos.response.badges.BadgeProgressResponse;
import dev.gamified.GamifiedPlatform.dtos.response.user.PublicUserProfileResponse;
import dev.gamified.GamifiedPlatform.dtos.response.user.UserBadgeResponse;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.repository.PlayerCharacterRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import dev.gamified.GamifiedPlatform.services.badge.GetBadgeProgressService;
import dev.gamified.GamifiedPlatform.services.badge.GetUserBadgesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Serviço para buscar o perfil público de um usuário com badges.
 * Inclui informações do personagem e conquistas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetPublicUserProfileService {

    private final UserRepository userRepository;
    private final PlayerCharacterRepository playerCharacterRepository;
    private final GetUserBadgesService getUserBadgesService;
    private final GetBadgeProgressService getBadgeProgressService;

    /*
     * Busca o perfil público completo de um usuário incluindo badges.
     * Requer permissão: próprio usuário, admin ou mentor.
     */
    @Transactional(readOnly = true)
    public PublicUserProfileResponse execute(Long userId) {
        log.info("Fetching public profile with badges for user {}", userId);

        User user = findUser(userId);
        PlayerCharacter character = findPlayerCharacter(userId);
        // Buscar todos os badges do usuário (máximo de 100 para o perfil público)
        Page<UserBadgeResponse> badgesPage = getUserBadgesService.execute(userId, PageRequest.of(0,
                100));
        List<UserBadgeResponse> badges = badgesPage.getContent();
        BadgeProgressResponse progress = getBadgeProgressService.execute(userId);

        return PublicUserProfileResponse.builder()
                .username(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .characterName(character != null ? character.getName() : null)
                .level(character != null ? character.getLevel() : null)
                .xp(character != null ? character.getXp() : null)
                .badges(badges)
                .badgeProgress(progress)
                .build();
    }

    /*
     * Busca o usuário ou lança exceção.
     */
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    /*
     * Busca o personagem do usuário (pode não existir).
     */
    private PlayerCharacter findPlayerCharacter(Long userId) {
        return playerCharacterRepository.findByUserId(userId).orElse(null);
    }
}

