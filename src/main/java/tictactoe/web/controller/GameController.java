package tictactoe.web.controller;


import tictactoe.domain.exception.GameNotFoundException;
import tictactoe.domain.model.WinsRatio;
import tictactoe.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.server.ResponseStatusException;
import tictactoe.web.mapper.GameMapper;
import tictactoe.domain.model.Game;
import tictactoe.domain.service.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tictactoe.web.model.GameDto;
import tictactoe.web.model.LeaderBoardDto;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    private final UserService userService;

    public GameController(GameService gameService,
                          UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<GameDto> createGame(@RequestParam(defaultValue = "true") boolean vsComputer) {
        UUID userId = getUserIdFromRequest();
        Game game = gameService.createGame(userId, vsComputer);
        return ResponseEntity.ok(GameMapper.toDto(game));
    }

    @GetMapping("/available")
    public ResponseEntity<List<GameDto>> getAvailableGames() {
        List<Game> games = gameService.getAvailableGames();
        List<GameDto> dtos = games.stream().map(GameMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{uuid}/join")
    public ResponseEntity<GameDto> joinGame(@PathVariable UUID uuid) {
        UUID userId = getUserIdFromRequest();
        Game game = gameService.joinGame(uuid, userId);
        return ResponseEntity.ok(GameMapper.toDto(game));
    }

    @PostMapping("/{uuid}")
    public ResponseEntity<GameDto> makeMove(
            @PathVariable UUID uuid,
            @Valid @RequestBody GameDto gameDto) {

        if (!uuid.equals(gameDto.getId())) {
            throw new IllegalArgumentException("UUID mismatch: path=" + uuid + ", body=" + gameDto.getId());
        }

        UUID userId = getUserIdFromRequest();
        Game updated = gameService.makeMove(uuid, GameMapper.toDomain(gameDto), userId);
        return ResponseEntity.ok(GameMapper.toDto(updated));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<GameDto> getGame(@PathVariable UUID uuid) {
        Game game = gameService.findGameById(uuid)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + uuid));
        return ResponseEntity.ok(GameMapper.toDto(game));
    }

    @GetMapping("/history")
    public ResponseEntity<List<GameDto>> getGameHistory() {
        UUID userId = getUserIdFromRequest();
        List<Game> games = gameService.getCompletedGames(userId);
        List<GameDto> dtos = games.stream().map(GameMapper::toDto).toList();
        return ResponseEntity.ok(dtos);
    }
    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderBoardDto>> getLeaderBoard(@RequestParam(defaultValue = "10") int n) {
        List<WinsRatio> top = gameService.getTopPlayers(n);
        List<LeaderBoardDto> dtos = top.stream().map(w -> userService.findById(w.getUserId())
                .map(u -> new LeaderBoardDto(w.getUserId(), u.getLogin(), w.getRatio()))
                .orElse(new LeaderBoardDto(w.getUserId(), null, w.getRatio()))).toList();
        return ResponseEntity.ok(dtos);
    }

    private UUID getUserIdFromRequest() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UUID id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        return id;
    }

}
