package tictactoe.datasource.mapper;

import tictactoe.domain.model.Board;
import tictactoe.domain.model.Game;
import tictactoe.datasource.model.BoardEntity;
import tictactoe.datasource.model.GameEntity;

public class  GameMapper {

    // domain → datasource (для сохранения)
    public static GameEntity toEntity(Game game) {
        Board board = game.getBoard();
        int[][] gridCopy = new int[3][3];
        for (int i = 0; i < 3; i++)
            System.arraycopy(board.getGrid()[i], 0, gridCopy[i], 0, 3);

        BoardEntity boardEntity = new BoardEntity(gridCopy);
        GameEntity entity = new GameEntity(game.getId(), boardEntity);
        entity.setStatus(game.getStatus());
        entity.setPlayerXId(game.getPlayerXId());
        entity.setPlayerOId(game.getPlayerOId());
        entity.setCurrentTurnId(game.getCurrentTurnId());
        entity.setWinnerId(game.getWinnerId());
        entity.setCreatedAt(game.getCreatedAt());
        return entity;
    }

    // datasource → domain (для чтения)
    public static Game toDomain(GameEntity entity) {
        BoardEntity boardEntity = entity.getBoard();
        int[][] gridCopy = new int[3][3];
        for (int i = 0; i < 3; i++)
            System.arraycopy(boardEntity.getGrid()[i], 0, gridCopy[i], 0, 3);

        Board board = new Board(gridCopy);
        return new Game(entity.getId(), board,
                entity.getStatus(), entity.getPlayerXId(), entity.getPlayerOId(),
                entity.getCurrentTurnId(), entity.getWinnerId(), entity.getCreatedAt());
    }
}