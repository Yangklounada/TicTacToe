package tictactoe.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;
import tictactoe.web.filter.AuthFilter;
import tictactoe.web.model.ApiError;
import tictactoe.web.security.JwtProvider;
import tictactoe.web.security.JwtUtil;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public SecurityConfig(JwtProvider jwtProvider, JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.jwtProvider = jwtProvider;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                 .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login", "/token/access", "/token/refresh").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint((request, response, authException) ->
                                writeApiError(response, HttpStatus.UNAUTHORIZED, "Authentication required"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                writeApiError(response, HttpStatus.FORBIDDEN, "Access denied"))
                )
                .addFilterBefore(new AuthFilter(jwtProvider, jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private void writeApiError(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(new ApiError(status, message)));
    }
}