package domain.model;

public class Board {
    private int[][] grid;

    public Board() {
        this.grid = new int[3][3];
    }

    public Board(Board other) {
        this.grid = new int[3][3];
        for (int i = 0; i < 3; i++ ){
            System.arraycopy(other.grid[i],0,this.grid[i],0,3);
        }
    }

    public Board(int[][] grid) {
        this.grid = new int[3][3];
        for (int i = 0; i < 3; i++ ){
            System.arraycopy(grid[i],0,this.grid[i],0,3);
        }
    }

    public int[][] getGrid() {
        int[][] copy = new int[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(this.grid[i], 0, copy[i], 0, 3);
        }
        return copy;
    }

    public void setCell(int row, int col, int val) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3) {
            this.grid[row][col] = val;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public int getCell(int row, int col) {
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
