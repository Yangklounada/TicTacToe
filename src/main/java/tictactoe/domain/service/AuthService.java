package tictactoe.domain.service;

import tictactoe.web.model.JwtRequest;
import tictactoe.web.model.JwtResponse;
import tictactoe.web.model.SignUpRequest;
import tictactoe.web.security.JwtAuthentication;

import java.util.UUID;

public interface AuthService {
    UUID register(SignUpRequest request);
    JwtResponse authorize(JwtRequest request);
    JwtResponse getAccessToken(String refreshToken);
    JwtResponse refreshToken(String refreshToken);
    JwtAuthentication getAuthentication();
}
