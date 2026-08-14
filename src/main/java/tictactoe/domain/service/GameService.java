package tictactoe.domain.service;

import tictactoe.domain.model.Game;
import tictactoe.domain.model.WinsRatio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameService {
    Optional<Game> findGameById(UUID id);
    Game createGame(UUID playerId, boolean vsComputer);
    Game joinGame(UUID gameId, UUID playerId);
    Game makeMove(UUID gameId, Game submittedGame, UUID playerId);
    List<Game> getAvailableGames();
    List<Game> getCompletedGames(UUID userId);
    List<WinsRatio> getTopPlayers(int n);
}
