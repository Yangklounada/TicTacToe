package tictactoe.domain.service;

import tictactoe.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User register(String login, String password);
    Optional<User> findByLogin(String login);
    Optional<User> findById(UUID id);
}
