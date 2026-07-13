package web.model;

import java.util.UUID;

public class GameDto {
    private UUID id;
    private BoardDto board;
    private String status;

    public GameDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public BoardDto getBoard() { return board; }
    public void setBoard(BoardDto board) { this.board = board; }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}