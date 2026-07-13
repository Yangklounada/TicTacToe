package datasource.repository;

import datasource.mapper.GameMapper;
import datasource.model.GameEntity;
import domain.model.Game;

import java.util.Optional;
import java.util.UUID;

public class GameRepositoryImpl implements  GameRepository{
    private final GameStore gameStore;

    public GameRepositoryImpl(GameStore gameStore) {
        this.gameStore = gameStore;
    }

    @Override
    public void save(Game game) {
        GameEntity entity = GameMapper.toEntity(game);
        gameStore.save(game.getId(), entity);
    }

    @Override
    public Optional<Game> findById(UUID id) {
        GameEntity entity = gameStore.findById(id);
        if (entity == null) return Optional.empty();
        return Optional.of(GameMapper.toDomain(entity));
    }
}
