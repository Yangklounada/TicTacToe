package tictactoe.datasource.repository;

import tictactoe.datasource.model.GameEntity;
import tictactoe.domain.model.GameStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GameEntityRepository extends CrudRepository<GameEntity, UUID> {
    List<GameEntity> findAllByStatus(GameStatus status);

    @Query("SELECT g FROM GameEntity g " +
            "WHERE (g.status = tictactoe.domain.model.GameStatus.WIN AND g.winnerId = :userId) " +
            "   OR (g.status = tictactoe.domain.model.GameStatus.DRAW " +
            "       AND (g.playerXId = :userId OR g.playerOId = :userId))")
    List<GameEntity> findCompletedGamesByUserId(@Param("userId") UUID userId);

    @Query(value = "WITH participants AS ( " +
            "    SELECT playerxid AS user_id, status, winner_id FROM games WHERE playerxid IS NOT NULL " +
            "    UNION ALL " +
            "    SELECT playeroid AS user_id, status, winner_id FROM games WHERE playeroid IS NOT NULL " +
            "), stats AS ( " +
            "    SELECT user_id, " +
            "           COUNT(*) FILTER (WHERE status = 'WIN' AND winner_id = user_id) AS wins, " +
            "           COUNT(*) FILTER (WHERE status = 'WIN' AND (winner_id IS NULL OR winner_id <> user_id)) " +
            "           + COUNT(*) FILTER (WHERE status = 'DRAW') AS losses_draws " +
            "    FROM participants " +
            "    WHERE status IN ('WIN', 'DRAW') " +
            "    GROUP BY user_id " +
            ") " +
            "SELECT user_id AS \"userId\", " +
            "       CASE WHEN losses_draws = 0 THEN wins " +
            "            ELSE CAST(wins AS DOUBLE PRECISION) / losses_draws END AS \"ratio\" " +
            "FROM stats " +
            "ORDER BY ratio DESC, user_id ASC " +
            "LIMIT :n", nativeQuery = true)
    List<PlayerRatioProjection> findTopPlayers(@Param("n") int n);
}