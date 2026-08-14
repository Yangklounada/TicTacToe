package tictactoe.datasource.model;

import tictactoe.domain.model.GameStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "games")
public class GameEntity {
    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "board_id")
    private BoardEntity board;
    @Enumerated(EnumType.STRING)
    private GameStatus status;
    private UUID playerXId;
    private UUID playerOId;
    private UUID currentTurnId;
    private UUID winnerId;
    private LocalDateTime createdAt;

    public GameEntity() {}

    public GameEntity(UUID id, BoardEntity board) {
        this.id = id;
        this.board = board;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public BoardEntity getBoard() { return board; }
    public void setBoard(BoardEntity board) { this.board = board; }

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