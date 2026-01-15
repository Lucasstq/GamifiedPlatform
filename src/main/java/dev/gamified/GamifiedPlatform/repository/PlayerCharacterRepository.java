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

    @Query(value = "SELECT " +
            "  (SELECT COUNT(*) + 1 FROM player_character pc " +
            "   WHERE pc.level > p.level OR (pc.level = p.level AND pc.xp > p.xp)) as position, " +
            "  (SELECT COUNT(*) FROM player_character) as totalPlayers " +
            "FROM player_character p WHERE p.id = :id",
            nativeQuery = true)
    RankingInfo findPlayerPosition(@Param("id") Long id);
}
