package dev.gamified.GamifiedPlatform.services.levels;

import dev.gamified.GamifiedPlatform.dtos.response.levels.LevelResponse;
import dev.gamified.GamifiedPlatform.mapper.LevelMapper;
import dev.gamified.GamifiedPlatform.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetLockedLevelsService {

    private final LevelRepository levelRepository;

    /**
     * Retorna os próximos níveis que ainda não foram desbloqueados paginados
     */
    public Page<LevelResponse> execute(Integer currentXp, Pageable pageable) {
        List<LevelResponse> allLockedLevels = levelRepository.findAllByOrderByOrderLevelAsc()
                .stream()
                .filter(level -> currentXp < level.getXpRequired())
                .map(LevelMapper::toResponse)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allLockedLevels.size());

        List<LevelResponse> pageContent = allLockedLevels.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allLockedLevels.size());
    }
}
