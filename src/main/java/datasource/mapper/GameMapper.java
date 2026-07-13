package datasource.mapper;

import domain.model.Board;
import domain.model.Game;
import datasource.model.BoardEntity;
import datasource.model.GameEntity;

public class GameMapper {

    // domain → datasource (для сохранения)
    public static GameEntity toEntity(Game game) {
        Board board = game.getBoard();
        int[][] gridCopy = new int[3][3];
        for (int i = 0; i < 3; i++)
            System.arraycopy(board.getGrid()[i], 0, gridCopy[i], 0, 3);

        BoardEntity boardEntity = new BoardEntity(gridCopy);
        return new GameEntity(game.getId(), boardEntity);
    }

    // datasource → domain (для чтения)
    public static Game toDomain(GameEntity entity) {
        BoardEntity boardEntity = entity.getBoard();
        int[][] gridCopy = new int[3][3];
        for (int i = 0; i < 3; i++)
            System.arraycopy(boardEntity.getGrid()[i], 0, gridCopy[i], 0, 3);

        Board board = new Board(gridCopy);
        return new Game(entity.getId(), board);
    }
}