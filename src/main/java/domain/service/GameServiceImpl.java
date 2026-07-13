package domain.service;

import datasource.repository.GameRepository;
import domain.model.Board;
import domain.model.Game;

import java.util.Optional;
import java.util.UUID;


public class GameServiceImpl implements GameService{
    final int EMPTY = 0;
    final int PLAYER = 1;
    final int COMPUTER = 2;

    private final GameRepository gameRepository;

    public GameServiceImpl(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public Game makeMove(Game game) {
        Board board = game.getBoard();
        int[] bestMove = findBestMove(board);
        if (bestMove == null) {
            return game;
        }
        Board newBoard = board.copyBoard();
        newBoard.setCell(bestMove[0], bestMove[1], COMPUTER);
        return new Game(game.getId(), newBoard);
    }

    @Override
    public boolean validateBoard(Game originalGame, Game updatedGame) {
        Board original = originalGame.getBoard();
        Board updated = updatedGame.getBoard();
        int changedCells = 0;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (original.getCell(i, j) != updated.getCell(i, j)) {
                    if (original.getCell(i, j) != EMPTY) return false;
                    if (updated.getCell(i, j) != PLAYER) return false;
                    changedCells++;
                }
            }
        }
        return changedCells == 1;
    }

    @Override
    public boolean isGameOver(Game game) {
        int winner = checkWinner(game.getBoard());
        if (winner != EMPTY) return true;
        return !isMoveLeft(game.getBoard());
    }

    @Override
    public Optional<Game> findGameById(UUID id) {
        return gameRepository.findById(id);
    }

    @Override
    public void saveGame(Game game) {
        gameRepository.save(game);
    }

    @Override
    public int checkWinner(Game game) {
        return checkWinner(game.getBoard());
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
}
