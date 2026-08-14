package tictactoe.datasource.repository;

import tictactoe.domain.model.Game;
import tictactoe.domain.model.GameStatus;
import tictactoe.domain.model.WinsRatio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameRepository {
    void save(Game game);
    Optional<Game> findById(UUID id);
    List<Game> findAllByStatus(GameStatus status);
    List<Game> findCompletedGamesByUserId(UUID id);
    List<WinsRatio> getTopPlayers(int n);
}
