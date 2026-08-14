package tictactoe.web.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import tictactoe.web.security.JwtAuthentication;
import tictactoe.web.security.JwtProvider;
import tictactoe.web.security.JwtUtil;

import java.io.IOException;

public class AuthFilter extends GenericFilterBean {

    private final JwtProvider jwtProvider;
    private final JwtUtil jwtUtil;

    public AuthFilter(JwtProvider jwtProvider, JwtUtil jwtUtil) {
        this.jwtProvider = jwtProvider;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            if (jwtProvider.validateAccessToken(token)) {
                Claims claims = jwtProvider.getAccessClaims(token);
                JwtAuthentication jwtAuthentication = jwtUtil.generate(claims);
                SecurityContextHolder.getContext().setAuthentication(jwtAuthentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}