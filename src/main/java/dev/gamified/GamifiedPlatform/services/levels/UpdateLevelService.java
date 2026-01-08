package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.domain.Levels;
import dev.gamified.GamifiedPlatform.dtos.request.level.LevelRequest;
import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.exceptions.BusinessException;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateLevelService {

    private final LevelRepository levelRepository;

    /**
     * Atualiza um nível existente
     * Somente administradores podem executar esta ação
     */
    @Transactional
    public LevelResponse execute(Long id, LevelRequest request) {

        Levels level = findLevelById(id);

        validateLevelOrder(level.getOrderLevel(), request, level);

        LevelMapper.updateEntityFromRequest(level, request);
        Levels updatedLevel = levelRepository.save(level);

        return LevelMapper.toResponse(updatedLevel);
    }

    private Levels findLevelById(Long id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Level with id " + id + " not found"));
    }

    // Se o orderLevel está sendo alterado, valida se não existe outro com esse número
    private void validateLevelOrder(Integer orderLevel, LevelRequest request, Levels level) {
        if (!level.getOrderLevel().equals(request.orderLevel())) {
            if (levelRepository.existsByOrderLevel(orderLevel)) {
                throw new BusinessException("Level with order " + orderLevel + " already exists");
            }
        }
    }

}
