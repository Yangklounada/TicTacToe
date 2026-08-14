package tictactoe.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Game {
    private UUID id;
    private Board board;
    private GameStatus status;
    private UUID playerXId;
    private UUID playerOId;
    private UUID currentTurnId;
    private UUID winnerId;
    private LocalDateTime createdAt;

    public Game(UUID id, Board board) {
        this.id = id;
        this.board = board;
    }

    public Game(UUID id, Board board, GameStatus status, UUID playerXId, UUID playerOId, UUID currentTurnId, UUID winnerId, LocalDateTime createdAt) {
        this.id = id;
        this.board = board;
        this.status = status;
        this.playerXId = playerXId;
        this.playerOId = playerOId;
        this.currentTurnId = currentTurnId;
        this.winnerId = winnerId;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

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
