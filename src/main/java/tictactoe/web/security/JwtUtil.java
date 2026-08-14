package tictactoe.web.security;

import tictactoe.domain.model.Role;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {
    public JwtAuthentication generate(Claims claims) {
        UUID id = UUID.fromString(claims.get("id", String.class));
        String role = claims.get("role", String.class);

        List<Role> roles = Arrays.stream(role.split(","))
                .map(Role::valueOf)
                .toList();

        JwtAuthentication jwtAuthentication = new JwtAuthentication(id, roles);
        jwtAuthentication.setAuthenticated(true);
        return jwtAuthentication;
    }
}
