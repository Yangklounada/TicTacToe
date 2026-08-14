package tictactoe.domain.model;

import java.util.UUID;

public class WinsRatio {
    private UUID userId;
    private double ratio;

    public WinsRatio() {}

    public WinsRatio(UUID userId, double ratio) {
        this.userId = userId;
        this.ratio = ratio;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public double getRatio() { return ratio; }
    public void setRatio(double ratio) { this.ratio = ratio; }
}