package tictactoe.domain.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import tictactoe.domain.model.Role;
import tictactoe.domain.model.User;
import tictactoe.web.model.JwtRequest;
import tictactoe.web.model.JwtResponse;
import tictactoe.web.model.SignUpRequest;
import tictactoe.web.security.JwtProvider;
import tictactoe.web.security.JwtUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

    private static final UUID ID = UUID.randomUUID();
    private static final User USER = new User(ID, "alice", "encoded", List.of(Role.USER));

    private UserService userService;
    private JwtProvider jwtProvider;
    private PasswordEncoder passwordEncoder;
    private AuthServiceImpl service;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        jwtProvider = mock(JwtProvider.class);
        JwtUtil jwtUtil = mock(JwtUtil.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new AuthServiceImpl(userService, jwtProvider, jwtUtil, passwordEncoder);
    }

    @Test
    void registerDelegatesToUserServiceAndReturnsId() {
        when(userService.register("alice", "secret")).thenReturn(USER);

        UUID id = service.register(signUpRequest("alice", "secret"));

        assertEquals(ID, id);
        verify(userService).register("alice", "secret");
    }

    @Test
    void authorizeReturnsTokensOnValidCredentials() {
        when(userService.findByLogin("alice")).thenReturn(Optional.of(USER));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        when(jwtProvider.generateAccessToken(USER)).thenReturn("access");
        when(jwtProvider.generateRefreshToken(USER)).thenReturn("refresh");

        JwtResponse response = service.authorize(new JwtRequest("alice", "secret"));

        assertEquals("access", response.getAccessToken());
        assertEquals("refresh", response.getRefreshToken());
    }

    @Test
    void authorizeThrowsWhenUserNotFound() {
        when(userService.findByLogin("alice")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.authorize(new JwtRequest("alice", "secret")));
        verify(jwtProvider, never()).generateAccessToken(anyUser());
    }

    @Test
    void authorizeThrowsOnWrongPassword() {
        when(userService.findByLogin("alice")).thenReturn(Optional.of(USER));
        when(passwordEncoder.matches("wrong", "encoded")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.authorize(new JwtRequest("alice", "wrong")));
        verify(jwtProvider, never()).generateAccessToken(anyUser());
    }

    @Test
    void getAccessTokenRotatesTokens() {
        Claims claims = mock(Claims.class);
        when(claims.get("id", String.class)).thenReturn(ID.toString());
        when(jwtProvider.validateRefreshToken("refresh")).thenReturn(true);
        when(jwtProvider.getRefreshClaims("refresh")).thenReturn(claims);
        when(userService.findById(ID)).thenReturn(Optional.of(USER));
        when(jwtProvider.generateAccessToken(USER)).thenReturn("new-access");
        when(jwtProvider.generateRefreshToken(USER)).thenReturn("new-refresh");

        JwtResponse response = service.getAccessToken("refresh");

        assertEquals("new-access", response.getAccessToken());
        assertEquals("new-refresh", response.getRefreshToken());
    }

    @Test
    void refreshTokenThrowsOnInvalidRefreshToken() {
        when(jwtProvider.validateRefreshToken("bad")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> service.refreshToken("bad"));
        verify(userService, never()).findById(anyId());
    }

    private static SignUpRequest signUpRequest(String login, String password) {
        SignUpRequest request = new SignUpRequest();
        request.setLogin(login);
        request.setPassword(password);
        return request;
    }

    private static User anyUser() {
        return org.mockito.ArgumentMatchers.any(User.class);
    }

    private static UUID anyId() {
        return org.mockito.ArgumentMatchers.any(UUID.class);
    }
}