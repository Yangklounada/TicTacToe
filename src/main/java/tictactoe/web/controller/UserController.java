package tictactoe.web.controller;

import org.springframework.http.HttpStatus;
import tictactoe.domain.service.AuthService;
import tictactoe.domain.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tictactoe.web.security.JwtAuthentication;

import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/user/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        JwtAuthentication auth = authService.getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UUID id)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authenticated");
        }
        return userService.findById(id)
                .map(user -> ResponseEntity.ok(new UserResponse(id, user.getLogin())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + id));
    }

    @GetMapping("/user/{uuid}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID uuid) {
        return userService.findById(uuid)
                .map(user -> ResponseEntity.ok(new UserResponse(uuid, user.getLogin())))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + uuid));
    }

    static class UserResponse {
        private UUID id;
        private String login;

        UserResponse(UUID id, String login) {
            this.id = id;
            this.login = login;
        }

        public UUID getId() {
            return id;
        }

        public String getLogin() {
            return login;
        }
    }
}