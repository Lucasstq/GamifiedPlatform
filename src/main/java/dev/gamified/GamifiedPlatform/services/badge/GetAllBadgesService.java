package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.domain.Badge;
import dev.gamified.GamifiedPlatform.dtos.response.BadgeResponse;
import dev.gamified.GamifiedPlatform.mapper.BadgeMapper;
import dev.gamified.GamifiedPlatform.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * Serviço para buscar todos os badges disponíveis no sistema.
 * Os badges são cacheados pois não mudam frequentemente.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@EnableCaching
public class GetAllBadgesService {

    private final BadgeRepository badgeRepository;

    /*
     * Busca todos os badges disponíveis no sistema.
     * Resultado é cacheado para melhor performance.
     */
    @Cacheable(value = "allBadges")
    @Transactional(readOnly = true)
    public List<BadgeResponse> execute() {
        log.info("Fetching all badges from database");

        List<Badge> badges = badgeRepository.findAll();

        return badges.stream()
                .map(BadgeMapper::toResponse)
                .toList();
    }
}
