package tictactoe.datasource.model;

import tictactoe.datasource.converter.IntArrayToJsonConverter;
import jakarta.persistence.*;

@Entity
@Table(name = "boards")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Convert(converter = IntArrayToJsonConverter.class)
    @Column(columnDefinition = "TEXT")
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
