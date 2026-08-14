package tictactoe.domain.service;

import org.springframework.stereotype.Service;
import tictactoe.datasource.repository.GameRepository;
import tictactoe.domain.exception.GameNotFoundException;
import tictactoe.domain.exception.IllegalMoveException;
import tictactoe.domain.model.Board;
import tictactoe.domain.model.Game;
import tictactoe.domain.model.GameStatus;
import tictactoe.domain.model.WinsRatio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameServiceImpl implements GameService{
    final int EMPTY = 0;
    final int PLAYER = 1;
    final int COMPUTER = 2;

    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    private Game computerMove(Game game) {
        if (game.getPlayerOId() != null) {
            return game;
        }
        Board board = game.getBoard();
        int[] bestMove = findBestMove(board);
        if (bestMove == null) {
            return game;
        }
        Board newBoard = board.copyBoard();
        newBoard.setCell(bestMove[0], bestMove[1], COMPUTER);
        return new Game(game.getId(), newBoard, game.getStatus(), game.getPlayerXId(), null, game.getCurrentTurnId(), game.getWinnerId(), game.getCreatedAt());
    }

    private boolean validateBoard(Game originalGame, Game updatedGame, UUID playerId) {
        Board original = originalGame.getBoard();
        Board updated = updatedGame.getBoard();

        int expectedToken;
        if (playerId.equals(originalGame.getPlayerXId())) {
            expectedToken = PLAYER;
        } else if (playerId.equals(originalGame.getPlayerOId())) {
            expectedToken = COMPUTER;
        } else {
            return false;
        }
        int changedCells = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (original.getCell(i, j) != updated.getCell(i, j)) {
                    if (original.getCell(i, j) != EMPTY) return false;
                    if (updated.getCell(i, j) != expectedToken) return false;
                    changedCells++;
                }
            }
        }
        return changedCells == 1;
    }

    @Override
    public Game makeMove(UUID gameId, Game submittedGame, UUID playerId) {
        Game original = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));

        if (original.getStatus() == GameStatus.DRAW || original.getStatus() == GameStatus.WIN) {
            throw new IllegalMoveException("Game already finished");
        }

        if (!validateBoard(original, submittedGame, playerId)) {
            throw new IllegalMoveException("Invalid move: you can only change one empty cell");
        }

        Game game = updateGameStatus(new Game(
                original.getId(),
                submittedGame.getBoard(),
                original.getStatus(),
                original.getPlayerXId(),
                original.getPlayerOId(),
                original.getCurrentTurnId(),
                original.getWinnerId(),
                original.getCreatedAt()));

        if (game.getStatus() == GameStatus.WIN || game.getStatus() == GameStatus.DRAW) {
            gameRepository.save(game);
            return game;
        }

        if (game.getPlayerOId() == null) {
            Game afterComputer = updateGameStatus(computerMove(game));
            gameRepository.save(afterComputer);
            return afterComputer;
        }

        UUID nextTurn = game.getPlayerXId().equals(game.getCurrentTurnId())
                ? game.getPlayerOId()
                : game.getPlayerXId();
        game.setCurrentTurnId(nextTurn);
        gameRepository.save(game);
        return game;
    }

    @Override
    public Optional<Game> findGameById(UUID id) {
        return gameRepository.findById(id);
    }


    @Override
    public Game createGame(UUID playerId, boolean vsComputer) {
        Board board = new Board();
        Game game;
        if (vsComputer) {
            game = new Game(UUID.randomUUID(), board,
                    GameStatus.IN_PROGRESS, playerId, null, playerId, null, LocalDateTime.now());

        } else {
            game = new Game(UUID.randomUUID(), board,
                    GameStatus.WAITING, playerId, null, playerId, null, LocalDateTime.now());
        }
        gameRepository.save(game);
        return game;
    }

    @Override
    public Game joinGame(UUID gameId, UUID playerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
        if (game.getStatus() != GameStatus.WAITING) {
            throw new IllegalMoveException("Game is not available to join");
        }
        game.setPlayerOId(playerId);
        game.setStatus(GameStatus.IN_PROGRESS);
        gameRepository.save(game);
        return game;
    }

    @Override
    public List<Game> getAvailableGames() {
        return gameRepository.findAllByStatus(GameStatus.WAITING);
    }

    private int[] findBestMove(Board board) {
        if (!isMoveLeft(board)) return null;
        int bestScore = -1;
        int[] bestMove = {-1, -1};

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getCell(i, j) == EMPTY) {
                    board.setCell(i, j, COMPUTER);
                    int score = miniMax(board, 0, false);
                    board.setCell(i, j, EMPTY);
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{i, j};
                    }
                }
            }
        }
        return bestMove;
    }

    private int miniMax(Board board, int depth, boolean isMaximizing) {

        int result = checkWinner(board);

        if (result == COMPUTER) return 10 - depth;
        if (result == PLAYER) return depth - 10;
        if (!isMoveLeft(board)) return 0;

        if (isMaximizing) {
            int bestScore = -1;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board.getCell(i, j) == EMPTY) {
                        board.setCell(i, j, COMPUTER);
                        int score = miniMax(board, depth + 1, false);
                        board.setCell(i, j, EMPTY);
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board.getCell(i, j) == EMPTY) {
                        board.setCell(i, j, PLAYER);
                        int score = miniMax(board, depth + 1, true);
                        board.setCell(i, j, EMPTY);
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }


    private boolean isMoveLeft(Board board) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board.getCell(i, j) == EMPTY) {
                    return true;
                }
            }
        }
        return false;
    }


    private int checkWinner(Board board) {
        for (int i = 0; i < 3; i++) {
            if (board.getCell(i, 0) == board.getCell(i, 1)
                    && board.getCell(i, 1) == board.getCell(i, 2)
                    && board.getCell(i, 0) != EMPTY)
                return board.getCell(i, 0);

            if (board.getCell(0, i) == board.getCell(1, i)
                    && board.getCell(1, i) == board.getCell(2, i)
                    && board.getCell(0, i) != EMPTY)
                return board.getCell(0, i);
        }
        if (board.getCell(0, 0) == board.getCell(1, 1)
                && board.getCell(1, 1) == board.getCell(2, 2)
                && board.getCell(0, 0) != EMPTY)
            return board.getCell(0, 0);

        if (board.getCell(0, 2) == board.getCell(1, 1)
                && board.getCell(1, 1) == board.getCell(2, 0)
                && board.getCell(2, 0) != EMPTY)
            return board.getCell(0, 2);

        return EMPTY;
    }

    private Game updateGameStatus(Game game) {
        int result = checkWinner(game.getBoard());
        if (result == PLAYER) {
            game.setStatus(GameStatus.WIN);
            game.setWinnerId(game.getPlayerXId());
        } else if (result == COMPUTER) {
            game.setStatus(GameStatus.WIN);
            if (game.getPlayerOId() != null) {
                game.setWinnerId(game.getPlayerOId());
            }
        } else if (!isMoveLeft(game.getBoard())){
            game.setStatus(GameStatus.DRAW);
        }
        return game;
    }

    @Override
    public List<Game> getCompletedGames(UUID userId) {
        return gameRepository.findCompletedGamesByUserId(userId);
    }

    @Override
    public List<WinsRatio> getTopPlayers(int n) {
        return gameRepository.getTopPlayers(n);
    }

}
