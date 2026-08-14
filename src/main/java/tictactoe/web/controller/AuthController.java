package tictactoe.web.controller;

import tictactoe.domain.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tictactoe.web.model.JwtRequest;
import tictactoe.web.model.JwtResponse;
import tictactoe.web.model.RefreshJwtRequest;
import tictactoe.web.model.SignUpRequest;

import java.util.UUID;

@RestController
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("register")
    public ResponseEntity<UUID> register(@RequestBody SignUpRequest request) {
        UUID userId = authService.register(request);
        return ResponseEntity.ok(userId);
    }

    @PostMapping("login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        JwtResponse response = authService.authorize(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("token/access")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        JwtResponse response = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("token/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        JwtResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}