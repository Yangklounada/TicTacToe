package web.mapper;

import domain.model.Board;
import domain.model.Game;
import web.model.BoardDto;
import web.model.GameDto;

public class GameMapper {

    public static Game toDomain(GameDto dto) {
        int[][] grid = dto.getBoard().getGrid();

        int[][] copy = new int[3][3];
        for (int i = 0; i < 3; i++)
            System.arraycopy(grid[i], 0, copy[i], 0, 3);

        Board board = new Board(copy);
        return new Game(dto.getId(), board);
    }

    public static GameDto toDto(Game game) {
        int[][] grid = game.getBoard().getGrid();

        int[][] copy = new int[3][3];
        for (int i = 0; i < 3; i++)
            System.arraycopy(grid[i], 0, copy[i], 0, 3);

        BoardDto boardDto = new BoardDto();
        boardDto.setGrid(copy);

        GameDto dto = new GameDto();
        dto.setId(game.getId());
        dto.setBoard(boardDto);
        return dto;
    }
}