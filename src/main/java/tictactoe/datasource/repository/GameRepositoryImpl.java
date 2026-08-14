package tictactoe.datasource.repository;

import org.springframework.stereotype.Repository;
import tictactoe.datasource.mapper.GameMapper;
import tictactoe.datasource.model.GameEntity;
import tictactoe.domain.model.Game;
import tictactoe.domain.model.GameStatus;
import tictactoe.domain.model.WinsRatio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class GameRepositoryImpl implements GameRepository{
    private final GameEntityRepository gameEntityRepository;

    public GameRepositoryImpl(GameEntityRepository gameEntityRepository) {
        this.gameEntityRepository = gameEntityRepository;
    }

    @Override
    public void save(Game game) {
        GameEntity entity = GameMapper.toEntity(game);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        gameEntityRepository.save(entity);
    }

    @Override
    public Optional<Game> findById(UUID id) {
        return gameEntityRepository.findById(id)
                .map(GameMapper::toDomain);
    }

    @Override
    public List<Game> findAllByStatus(GameStatus status) {
        return gameEntityRepository.findAllByStatus(status).stream().map(GameMapper::toDomain).toList();
    }

    @Override
    public List<Game> findCompletedGamesByUserId(UUID id) {
        return gameEntityRepository.findCompletedGamesByUserId(id)
                .stream().map(GameMapper::toDomain).toList();
    }


    @Override
    public List<WinsRatio> getTopPlayers(int n) {
        return gameEntityRepository.findTopPlayers(n)
                .stream().map(p -> new WinsRatio(p.getUserId(), p.getRatio())).toList();
    }
}
