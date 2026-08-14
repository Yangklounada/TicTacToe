package tictactoe.domain.service;

import org.springframework.stereotype.Service;
import tictactoe.domain.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tictactoe.web.model.JwtRequest;
import tictactoe.web.model.JwtResponse;
import tictactoe.web.model.SignUpRequest;
import tictactoe.web.security.JwtAuthentication;
import tictactoe.web.security.JwtProvider;
import tictactoe.web.security.JwtUtil;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserService userService, JwtProvider jwtProvider, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UUID register(SignUpRequest request) {
        User user = userService.register(request.getLogin(), request.getPassword());
        return user.getId();
    }

    @Override
    public JwtResponse authorize(JwtRequest request) {
        User user = userService.findByLogin(request.getLogin())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        String accessToken = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user);
        return new JwtResponse(accessToken, refreshToken);
    }

    @Override
    public JwtResponse getAccessToken(String refreshToken) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        UUID id = UUID.fromString(claims.get("id", String.class));
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);
        return new JwtResponse(accessToken, newRefreshToken);
    }

    @Override
    public JwtResponse refreshToken(String refreshToken) {
        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        UUID id = UUID.fromString(claims.get("id", String.class));
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken = jwtProvider.generateAccessToken(user);
        String newRefreshToken = jwtProvider.generateRefreshToken(user);
        return new JwtResponse(accessToken, newRefreshToken);
    }

    @Override
    public JwtAuthentication getAuthentication() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }
}
