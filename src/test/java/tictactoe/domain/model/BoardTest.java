package tictactoe.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BoardTest {

    @Test
    void emptyBoardIsFilledWithEmptyCells() {
        Board board = new Board();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                assertEquals(Cell.EMPTY, board.getCell(i, j));
            }
        }
    }

    @Test
    void setAndGetCellWork() {
        Board board = new Board();
        board.setCell(1, 2, Cell.X);
        assertEquals(Cell.X, board.getCell(1, 2));
    }

    @Test
    void getGridReturnsDefensiveCopy() {
        Board board = new Board();
        Cell[][] grid = board.getGrid();
        grid[0][0] = Cell.O;

        assertEquals(Cell.EMPTY, board.getCell(0, 0));
    }

    @Test
    void copyConstructorIsDeepCopy() {
        Board board = new Board();
        board.setCell(0, 0, Cell.X);

        Board copy = new Board(board);
        copy.setCell(0, 0, Cell.O);

        assertEquals(Cell.X, board.getCell(0, 0));
        assertEquals(Cell.O, copy.getCell(0, 0));
        assertNotSame(board.getGrid(), copy.getGrid());
    }

    @Test
    void setCellOutOfBoundsThrows() {
        Board board = new Board();
        assertThrows(IndexOutOfBoundsException.class, () -> board.setCell(3, 0, Cell.X));
        assertThrows(IndexOutOfBoundsException.class, () -> board.setCell(0, -1, Cell.X));
    }

    @Test
    void getCellOutOfBoundsThrows() {
        Board board = new Board();
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(10, 0));
    }
}