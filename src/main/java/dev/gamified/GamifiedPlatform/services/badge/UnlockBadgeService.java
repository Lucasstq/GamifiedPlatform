package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.domain.Badge;
import dev.gamified.GamifiedPlatform.domain.User;
import dev.gamified.GamifiedPlatform.domain.UserBadge;
import dev.gamified.GamifiedPlatform.dtos.response.UserBadgeResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.BadgeMapper;
import dev.gamified.GamifiedPlatform.repository.BadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserBadgeRepository;
import dev.gamified.GamifiedPlatform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/*
 * Serviço responsável por desbloquear badges para usuários.
 * Automaticamente chamado quando um usuário derrota um boss.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UnlockBadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final UserRepository userRepository;

    /*
     * Desbloqueia um badge para um usuário ao derrotar um boss.
     * Verifica se o badge já foi desbloqueado antes de criar.
     */
    @Transactional
    public UserBadgeResponse execute(Long userId, Long levelId, Long bossId) {
        log.info("Attempting to unlock badge for user {} after defeating boss {} at level {}",
                userId, bossId, levelId);

        User user = findUser(userId);
        Badge badge = findBadgeByLevel(levelId);

        // Verifica se o usuário já possui este badge
        if (userBadgeRepository.existsByUserIdAndBadgeId(userId, badge.getId())) {
            log.warn("User {} already has badge {}", userId, badge.getName());
            throw new BusinessException("Badge already unlocked for this user");
        }

        UserBadge userBadge = UserBadge.builder()
                .user(user)
                .badge(badge)
                .unlockedAt(LocalDateTime.now())
                .unlockedByBossId(bossId)
                .build();

        UserBadge savedUserBadge = userBadgeRepository.save(userBadge);

        log.info("Badge '{}' unlocked for user {}", badge.getName(), userId);

        return BadgeMapper.toUserBadgeResponse(savedUserBadge);
    }

    /*
     * Busca o usuário ou lança exceção se não encontrado.
     */
    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    /*
     * Busca o badge associado ao nível ou lança exceção se não encontrado.
     */
    private Badge findBadgeByLevel(Long levelId) {
        return badgeRepository.findByLevelId(levelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Badge not found for level ID: " + levelId));
    }
}
