package dev.gamified.GamifiedPlatform.services.boss;

import dev.gamified.GamifiedPlatform.dtos.response.BossResponse;
import dev.gamified.GamifiedPlatform.mapper.BossMapper;
import dev.gamified.GamifiedPlatform.repository.BossRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j

public class GetAllBossesService {

    private final BossRepository bossRepository;

    @Transactional(readOnly = true)
    public Page<BossResponse> execute(Pageable pageable) {
        log.info("Getting all bosses with pagination");
        return bossRepository.findAll(pageable)
                .map(BossMapper::toResponse);
    }
}
