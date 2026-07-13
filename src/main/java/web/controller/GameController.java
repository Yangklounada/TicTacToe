package web.controller;


import domain.model.Board;
import web.mapper.GameMapper;
import domain.model.Game;
import domain.service.GameService;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.model.GameDto;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<GameDto> createGame() {
        Game game = new Game(UUID.randomUUID(), new Board());
        gameService.saveGame(game);
        GameDto dto = GameMapper.toDto(game);
        dto.setStatus("PLAYING");
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{uuid}")
    public ResponseEntity<?> makeMove(
            @PathVariable UUID uuid,
            @RequestBody GameDto gameDto) {

        if (!uuid.equals(gameDto.getId())) {
            return ResponseEntity.badRequest()
                    .body("UUID mismatch: path=" + uuid + ", body=" + gameDto.getId());
        }
        Game updatedGame = GameMapper.toDomain(gameDto);

        Optional<Game> originalOpt = gameService.findGameById(uuid);
        if (originalOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Game not found: " + uuid);
        }
        Game originalGame = originalOpt.get();
        if (gameService.isGameOver(originalGame)) {
            return ResponseEntity.badRequest().body("Game already finished: " + uuid);
        }

        if (!gameService.validateBoard(originalGame, updatedGame)) {
            return ResponseEntity.badRequest().body("Invalid move: you can only change one empty cell");
        }

        if (gameService.isGameOver(updatedGame)) {
            GameDto responseDto = GameMapper.toDto(updatedGame);
            int winner = gameService.checkWinner(updatedGame);
            responseDto.setStatus(winner == 1 ? "PLAYER_WON" : "DRAW");
            return ResponseEntity.ok(responseDto);
        }

        Game gameAfterComputer = gameService.makeMove(updatedGame);
        gameService.saveGame(gameAfterComputer);

        GameDto responseDto = GameMapper.toDto(gameAfterComputer);
        if (gameService.isGameOver(gameAfterComputer)) {
            int winner = gameService.checkWinner(gameAfterComputer);
            if (winner == 2) {
                responseDto.setStatus("COMPUTER_WON");
            } else {
                responseDto.setStatus("DRAW");
            }
        } else {
            responseDto.setStatus("PLAYING");
        }

        return ResponseEntity.ok(responseDto);
    }

}
