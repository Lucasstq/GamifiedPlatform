package dev.gamified.GamifiedPlatform.services.badge;

import dev.gamified.GamifiedPlatform.dtos.response.badges.BadgeResponse;
import dev.gamified.GamifiedPlatform.mapper.BadgeMapper;
import dev.gamified.GamifiedPlatform.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * Busca todos os badges disponíveis no sistema paginados.
     * Resultado é cacheado para melhor performance.
     */
    @Cacheable(value = "allBadges", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    @Transactional(readOnly = true)
    public Page<BadgeResponse> execute(Pageable pageable) {
        log.info("Fetching all badges from database - página: {}, tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        return badgeRepository.findAll(pageable)
                .map(BadgeMapper::toResponse);
    }
}
