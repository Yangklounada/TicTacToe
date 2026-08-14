package tictactoe.domain.service;

import org.springframework.stereotype.Service;
import tictactoe.datasource.model.UserEntity;
import tictactoe.datasource.repository.UserEntityRepository;
import tictactoe.domain.model.Role;
import tictactoe.domain.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserEntityRepository userEntityRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserEntityRepository userEntityRepository, PasswordEncoder passwordEncoder) {
        this.userEntityRepository = userEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User register(String login, String password) {
        UserEntity entity = new UserEntity(UUID.randomUUID(), login, passwordEncoder.encode(password));
        entity.setRoles(List.of(Role.USER));
        userEntityRepository.save(entity);
        return new User(entity.getId(), entity.getLogin(), entity.getPassword(), entity.getRoles());

    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userEntityRepository.findByLogin(login)
                .map(e -> new User(e.getId(), e.getLogin(), e.getPassword(), e.getRoles()));
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userEntityRepository.findById(id)
                .map(e -> new User(e.getId(), e.getLogin(), e.getPassword(), e.getRoles()));
    }
}
