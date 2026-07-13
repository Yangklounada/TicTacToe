package di;

import datasource.repository.GameRepository;
import datasource.repository.GameRepositoryImpl;
import datasource.repository.GameStore;
import domain.service.GameService;
import domain.service.GameServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {
    @Bean
    public GameStore gameStore() {
        return new GameStore();
    }
    @Bean
    public GameRepository gameRepository(GameStore gameStore) {
        return new GameRepositoryImpl(gameStore);
    }

    @Bean
    public GameService gameService(GameRepository gameRepository) {
        return new GameServiceImpl(gameRepository);
    }
}
