package dev.gamified.GamifiedPlatform.services.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {

    private final RefreshTokenService refreshTokenService;

    // Executa diariamente às 3 da manhã
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredTokens() {
        refreshTokenService.cleanupExpiredTokens();
    }

}
