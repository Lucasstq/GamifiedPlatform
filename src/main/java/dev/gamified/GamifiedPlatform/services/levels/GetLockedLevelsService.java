package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.dtos.response.LevelResponse;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetLockedLevelsService {

    private final LevelRepository levelRepository;

    /**
     * Retorna os próximos níveis que ainda não foram desbloqueados
     */
    public List<LevelResponse> execute(Integer currentXp) {
        return levelRepository.findAllByOrderByOrderLevelAsc()
                .stream()
                .filter(level -> currentXp < level.getXpRequired())
                .map(LevelMapper::toResponse)
                .collect(Collectors.toList());
    }
}
