package datasource.model;

public class BoardEntity {
    private int[][] grid;

    public BoardEntity() {
        this.grid = new int[3][3];
    }

    public BoardEntity(int[][] grid) {
        this.grid = grid;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }
}
