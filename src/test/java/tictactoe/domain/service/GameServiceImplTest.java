package tictactoe.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tictactoe.datasource.repository.GameRepository;
import tictactoe.domain.exception.IllegalMoveException;
import tictactoe.domain.model.Board;
import tictactoe.domain.model.Cell;
import tictactoe.domain.model.Game;
import tictactoe.domain.model.GameStatus;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameServiceImplTest {

    private static final UUID GAME_ID = UUID.randomUUID();
    private static final UUID X = UUID.randomUUID();
    private static final UUID O = UUID.randomUUID();

    private GameRepository repository;
    private GameServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(GameRepository.class);
        service = new GameServiceImpl(repository);
    }

    @Test
    void createGameVsComputerStartsInProgress() {
        Game game = service.createGame(X, true);

        assertEquals(GameStatus.IN_PROGRESS, game.getStatus());
        assertNull(game.getPlayerOId());
        assertEquals(X, game.getCurrentTurnId());
        verify(repository).save(any());
    }

    @Test
    void createGameWaitingForOpponent() {
        Game game = service.createGame(X, false);

        assertEquals(GameStatus.WAITING, game.getStatus());
        assertNull(game.getPlayerOId());
        verify(repository).save(any());
    }

    @Test
    void validMoveSwitchesTurnToOpponent() {
        Game original = game(emptyGrid(), GameStatus.IN_PROGRESS, X, O, X, null);
        when(repository.findById(GAME_ID)).thenReturn(java.util.Optional.of(original));

        Board moved = new Board();
        moved.setCell(0, 0, Cell.X);
        Game result = service.makeMove(GAME_ID, new Game(GAME_ID, moved), X);

        assertEquals(Cell.X, result.getBoard().getCell(0, 0));
        assertEquals(O, result.getCurrentTurnId());
        verify(repository).save(any());
    }

    @Test
    void moveChangingTwoCellsIsRejected() {
        Game original = game(emptyGrid(), GameStatus.IN_PROGRESS, X, O, X, null);
        when(repository.findById(GAME_ID)).thenReturn(java.util.Optional.of(original));

        Board moved = new Board();
        moved.setCell(0, 0, Cell.X);
        moved.setCell(1, 1, Cell.X);

        assertThrows(IllegalMoveException.class, () -> service.makeMove(GAME_ID, new Game(GAME_ID, moved), X));
    }

    @Test
    void moveIntoOccupiedCellIsRejected() {
        Game original = game(grid(new int[][]{{1, 0, 0}, {0, 0, 0}, {0, 0, 0}}),
                GameStatus.IN_PROGRESS, X, O, X, null);
        when(repository.findById(GAME_ID)).thenReturn(java.util.Optional.of(original));

        Board moved = new Board();
        moved.setCell(0, 0, Cell.O);

        assertThrows(IllegalMoveException.class, () -> service.makeMove(GAME_ID, new Game(GAME_ID, moved), X));
    }

    @Test
    void moveByNonParticipantIsRejected() {
        UUID stranger = UUID.randomUUID();
        Game original = game(emptyGrid(), GameStatus.IN_PROGRESS, X, O, X, null);
        when(repository.findById(GAME_ID)).thenReturn(java.util.Optional.of(original));

        Board moved = new Board();
        moved.setCell(0, 0, Cell.X);

        assertThrows(IllegalMoveException.class, () -> service.makeMove(GAME_ID, new Game(GAME_ID, moved), stranger));
    }

    @Test
    void moveOnFinishedGameIsRejected() {
        Game original = game(emptyGrid(), GameStatus.WIN, X, O, X, X);
        when(repository.findById(GAME_ID)).thenReturn(java.util.Optional.of(original));

        Board moved = new Board();
        moved.setCell(0, 0, Cell.X);

        assertThrows(IllegalMoveException.class, () -> service.makeMove(GAME_ID, new Game(GAME_ID, moved), X));
    }

    @Test
    void computerRespondsWithOWhenPlayingSolo() {
        Game original = game(emptyGrid(), GameStatus.IN_PROGRESS, X, null, X, null);
        when(repository.findById(GAME_ID)).thenReturn(java.util.Optional.of(original));

        Board moved = new Board();
        moved.setCell(0, 0, Cell.X);
        Game result = service.makeMove(GAME_ID, new Game(GAME_ID, moved), X);

        assertEquals(Cell.X, result.getBoard().getCell(0, 0));
        assertEquals(1, count(result, Cell.O));
        assertEquals(X, result.getCurrentTurnId());
        verify(repository).save(any());
    }

    @Test
    void winIsDetectedAndWinnerRecorded() {
        Game original = game(grid(new int[][]{{1, 1, 0}, {0, 2, 0}, {0, 0, 0}}),
                GameStatus.IN_PROGRESS, X, O, X, null);
        when(repository.findById(GAME_ID)).thenReturn(java.util.Optional.of(original));

        Board moved = new Board(original.getBoard());
        moved.setCell(0, 2, Cell.X);
        Game result = service.makeMove(GAME_ID, new Game(GAME_ID, moved), X);

        assertEquals(GameStatus.WIN, result.getStatus());
        assertEquals(X, result.getWinnerId());
    }

    @Test
    void drawIsDetectedWhenBoardIsFull() {
        Game original = game(grid(new int[][]{{1, 2, 1}, {1, 2, 2}, {2, 1, 0}}),
                GameStatus.IN_PROGRESS, X, O, X, null);
        when(repository.findById(GAME_ID)).thenReturn(java.util.Optional.of(original));

        Board moved = new Board(original.getBoard());
        moved.setCell(2, 2, Cell.X);
        Game result = service.makeMove(GAME_ID, new Game(GAME_ID, moved), X);

        assertEquals(GameStatus.DRAW, result.getStatus());
        assertNull(result.getWinnerId());
    }

    @Test
    void joinMakesWaitingGameInProgress() {
        Game original = game(emptyGrid(), GameStatus.WAITING, X, null, X, null);
        when(repository.findById(GAME_ID)).thenReturn(java.util.Optional.of(original));

        Game result = service.joinGame(GAME_ID, O);

        assertEquals(O, result.getPlayerOId());
        assertEquals(GameStatus.IN_PROGRESS, result.getStatus());
        verify(repository).save(any());
    }

    @Test
    void joinNonWaitingGameIsRejected() {
        Game original = game(emptyGrid(), GameStatus.IN_PROGRESS, X, null, X, null);
        when(repository.findById(GAME_ID)).thenReturn(java.util.Optional.of(original));

        assertThrows(IllegalMoveException.class, () -> service.joinGame(GAME_ID, O));
    }

    private Game game(Cell[][] grid, GameStatus status, UUID x, UUID o, UUID turn, UUID winner) {
        return new Game(GAME_ID, new Board(grid), status, x, o, turn, winner, LocalDateTime.now());
    }

    private static Cell[][] emptyGrid() {
        return grid(new int[3][3]);
    }

    private static Cell[][] grid(int[][] ints) {
        Cell[][] cells = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                cells[i][j] = Cell.fromValue(ints[i][j]);
            }
        }
        return cells;
    }

    private static int count(Game game, Cell cell) {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (game.getBoard().getCell(i, j) == cell) {
                    count++;
                }
            }
        }
        return count;
    }
}