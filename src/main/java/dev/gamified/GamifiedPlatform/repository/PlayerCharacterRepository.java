package dev.gamified.GamifiedPlatform.repository;

import dev.gamified.GamifiedPlatform.domain.PlayerCharacter;
import dev.gamified.GamifiedPlatform.dtos.response.ranking.RankingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerCharacterRepository extends JpaRepository<PlayerCharacter, Long> {

    Optional<PlayerCharacter> findByUserId(Long userId);

    @Query("SELECT pc FROM PlayerCharacter pc LEFT JOIN FETCH pc.user WHERE pc.id IN :ids")
    List<PlayerCharacter> findAllByIdInWithUser(@Param("ids") List<Long> ids);

    @Query("SELECT new dev.gamified.GamifiedPlatform.dtos.response.ranking.RankingInfo(" +
            "  (SELECT COUNT(pc) + 1 FROM PlayerCharacter pc " +
            "   WHERE pc.level > p.level OR (pc.level = p.level AND pc.xp > p.xp)), " +
            "  (SELECT COUNT(pc2) FROM PlayerCharacter pc2)) " +
            "FROM PlayerCharacter p WHERE p.id = :id")
    RankingInfo findPlayerPosition(@Param("id") Long id);
}
