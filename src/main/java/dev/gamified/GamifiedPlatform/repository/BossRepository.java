package dev.gamified.GamifiedPlatform.repository;
import dev.gamified.GamifiedPlatform.domain.Boss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BossRepository extends JpaRepository<Boss, Long> {
    Optional<Boss> findByLevelId(Long levelId);
    boolean existsByLevelId(Long levelId);
}
