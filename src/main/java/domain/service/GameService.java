package domain.service;

import domain.model.Game;

import java.util.Optional;
import java.util.UUID;

public interface GameService {
    Game makeMove(Game game);
    boolean validateBoard(Game originalGame, Game updatedGame);
    boolean isGameOver(Game game);
    Optional<Game> findGameById(UUID id);
    void saveGame(Game game);
    int checkWinner(Game game);
}
