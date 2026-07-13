package datasource.repository;

import datasource.model.GameEntity;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GameStore {
    private final ConcurrentHashMap<UUID, GameEntity> games = new ConcurrentHashMap<>();

    public void save(UUID id, GameEntity game) {
        games.put(id, game);
    }

    public GameEntity findById(UUID id) {
        return games.get(id);
    }

    public boolean containsKey(UUID id) {
        return games.containsKey(id);
    }
}
