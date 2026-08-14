package tictactoe.datasource.repository;

import java.util.UUID;

public interface PlayerRatioProjection {
    UUID getUserId();
    Double getRatio();

}