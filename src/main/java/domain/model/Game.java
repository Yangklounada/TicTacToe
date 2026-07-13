package domain.model;

import java.util.UUID;

public class Game {
    private UUID id;
    private Board board;

    public Game(UUID id, Board board) {
        this.id = id;
        this.board = board;
    }

    public UUID getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }
}
