package tictactoe.datasource.repository;

import tictactoe.datasource.model.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends CrudRepository<UserEntity, UUID> {
    Optional<UserEntity> findByLogin(String login);
}
