package tictactoe.web.mapper;

import tictactoe.domain.model.Board;
import tictactoe.domain.model.Cell;
import tictactoe.domain.model.Game;
import tictactoe.web.model.BoardDto;
import tictactoe.web.model.GameDto;

public class GameMapper {

    public static Game toDomain(GameDto dto) {
        int[][] grid = dto.getBoard().getGrid();
        Cell[][] cells = new Cell[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                cells[i][j] = Cell.fromValue(grid[i][j]);

        Board board = new Board(cells);
        return new Game(dto.getId(), board,
                dto.getStatus(), dto.getPlayerXId(), dto.getPlayerOId(),
                dto.getCurrentTurnId(), dto.getWinnerId(), dto.getCreatedAt());
    }

    public static GameDto toDto(Game game) {
        Cell[][] grid = game.getBoard().getGrid();

        int[][] copy = new int[3][3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                copy[i][j] = grid[i][j].toValue();

        BoardDto boardDto = new BoardDto();
        boardDto.setGrid(copy);

        GameDto dto = new GameDto();
        dto.setId(game.getId());
        dto.setBoard(boardDto);
        dto.setStatus(game.getStatus());
        dto.setPlayerXId(game.getPlayerXId());
        dto.setPlayerOId(game.getPlayerOId());
        dto.setCurrentTurnId(game.getCurrentTurnId());
        dto.setWinnerId(game.getWinnerId());
        dto.setCreatedAt(game.getCreatedAt());
        return dto;
    }
}