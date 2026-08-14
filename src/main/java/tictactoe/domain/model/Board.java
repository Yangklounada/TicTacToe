package tictactoe.domain.model;

import java.util.Arrays;

public class Board {
    private Cell[][] grid;

    public Board() {
        this.grid = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            Arrays.fill(this.grid[i], Cell.EMPTY);
        }
    }

    public Board(Board other) {
        this.grid = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(other.grid[i], 0, this.grid[i], 0, 3);
        }
    }

    public Board(Cell[][] grid) {
        this.grid = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(grid[i], 0, this.grid[i], 0, 3);
        }
    }

    public Cell[][] getGrid() {
        Cell[][] copy = new Cell[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(this.grid[i], 0, copy[i], 0, 3);
        }
        return copy;
    }

    public void setCell(int row, int col, Cell val) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            this.grid[row][col] = val;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public Cell getCell(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            return this.grid[row][col];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public Board copyBoard() {
        return new Board(this);
    }
}