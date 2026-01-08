package dev.gamified.GamifiedPlatform.services.boss;

import dev.gamified.GamifiedPlatform.domain.Boss;
import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.request.boss.BossCreateRequest;
import dev.gamified.GamifiedPlatform.dtos.response.bosses.BossResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.exceptions.ResourceNotFoundException;
import dev.gamified.GamifiedPlatform.mapper.BossMapper;
import dev.gamified.GamifiedPlatform.repository.BossRepository;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 * Serviço responsável por criar um novo boss.
 * Apenas admins podem criar bosses.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateBossService {

    private final BossRepository bossRepository;
    private final LevelRepository levelRepository;

    /*
     * Cria um novo boss para um nível específico.
     */
    @Transactional
    public BossResponse execute(BossCreateRequest request) {
        log.info("Creating new boss for level {}", request.levelId());

        // Busca o nível
        Levels level = findLevel(request.levelId());

        // Valida se já existe boss para este nível
        validateNoBossExists(level.getId());

        // Cria a entidade Boss usando o mapper
        Boss boss = BossMapper.toEntity(request, level);

        // Salva o boss
        Boss savedBoss = bossRepository.save(boss);
        log.info("Boss created successfully: {} for level {}", savedBoss.getName(), level.getName());

        return BossMapper.toResponse(savedBoss);
    }

    /*
     * Busca um nível pelo ID.
     */
    private Levels findLevel(Long levelId) {
        return levelRepository.findById(levelId)
                .orElseThrow(() -> new ResourceNotFoundException("Level not found by ID: " + levelId));
    }

    /*
     * Valida que não existe um boss para o nível especificado.
     * Cada nível só pode ter um boss.
     */
    private void validateNoBossExists(Long levelId) {
        if (bossRepository.existsByLevelId(levelId)) {
            throw new BusinessException("A boss already exists for this level. Each level can only have one boss.");
        }
    }
}

