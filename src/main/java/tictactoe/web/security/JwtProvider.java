package tictactoe.web.security;

import tictactoe.domain.model.Role;
import tictactoe.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtProvider(
            @Value("${jwt.access-secret}") String accessSecret,
            @Value("${jwt.refresh-secret}") String refreshSecret,
            @Value("${jwt.access-expiration-ms}") long accessExpiration,
            @Value("${jwt.refresh-expiration-ms}") long refreshExpiration) {
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes());
        this.refreshKey = Keys.hmacShaKeyFor(refreshSecret.getBytes());
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId().toString());
        claims.put("role", user.getRoles().stream()
                .map(Role::getAuthority)
                .collect(Collectors.joining(",")));

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(accessKey)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId().toString());

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(refreshKey)
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, accessKey);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshKey);
    }

    private boolean validateToken(String token, SecretKey key) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims getAccessClaims(String token) {
        return Jwts.parser().verifyWith(accessKey).build()
                .parseSignedClaims(token).getPayload();
    }

    public Claims getRefreshClaims(String token) {
        return Jwts.parser().verifyWith(refreshKey).build()
                .parseSignedClaims(token).getPayload();
    }
}