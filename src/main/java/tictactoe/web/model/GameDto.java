package tictactoe.web.model;

import jakarta.validation.constraints.NotNull;
import tictactoe.domain.model.GameStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class GameDto {
    @NotNull(message = "id must not be null")
    private UUID id;
    private BoardDto board;
    private GameStatus status;
    private UUID playerXId;
    private UUID playerOId;
    private UUID currentTurnId;
    private UUID winnerId;
    private LocalDateTime createdAt;

    public GameDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public BoardDto getBoard() { return board; }
    public void setBoard(BoardDto board) { this.board = board; }

    public GameStatus getStatus() {
        return status;
    }
    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public UUID getPlayerXId() {
        return playerXId;
    }

    public void setPlayerXId(UUID playerXId) {
        this.playerXId = playerXId;
    }

    public UUID getPlayerOId() {
        return playerOId;
    }

    public void setPlayerOId(UUID playerOId) {
        this.playerOId = playerOId;
    }

    public UUID getCurrentTurnId() {
        return currentTurnId;
    }

    public void setCurrentTurnId(UUID currentTurnId) {
        this.currentTurnId = currentTurnId;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}