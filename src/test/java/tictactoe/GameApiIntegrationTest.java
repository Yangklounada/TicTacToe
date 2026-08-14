package tictactoe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.IntNode;
import tools.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class GameApiIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18-alpine");

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void protectedEndpointRequiresToken() throws Exception {
        mockMvc.perform(get("/game/available"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registrationAndLoginReturnToken() throws Exception {
        String userId = register("auth_flow_user");
        String token = login("auth_flow_user");

        mockMvc.perform(get("/game/available").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        assertThat(userId).isNotNull();
        assertThat(token).isNotBlank();
    }

    @Test
    void invalidRegistrationIsRejected() throws Exception {
        mockMvc.perform(post("/register").contentType(APPLICATION_JSON)
                        .content("{\"login\":\"ab\",\"password\":\"x\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("login")));
    }

    @Test
    void pvpGameEndsWithWinAndHistoryAndLeaderboard() throws Exception {
        String p1 = register("pvp_p1");
        String p2 = register("pvp_p2");
        String tok1 = login("pvp_p1");
        String tok2 = login("pvp_p2");

        JsonNode game = createGame(tok1, false);
        String gameId = game.get("id").asText();

        mockMvc.perform(post("/game/" + gameId + "/join").header("Authorization", "Bearer " + tok2))
                .andExpect(status().isOk());

        int[][] cells = {{1, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        game = setCell(game, 0, 0, 1);
        move(tok1, gameId, game);
        game = setCell(game, 0, 1, 2);
        move(tok2, gameId, game);
        game = setCell(game, 1, 0, 1);
        move(tok1, gameId, game);
        game = setCell(game, 1, 1, 2);
        move(tok2, gameId, game);
        game = setCell(game, 2, 0, 1);
        JsonNode finalState = move(tok1, gameId, game);

        assertThat(finalState.get("status").asText()).isEqualTo("WIN");
        assertThat(finalState.get("winnerId").asText()).isEqualTo(p1);

        JsonNode history = getJson("/game/history", tok1);
        boolean containsGame = false;
        for (JsonNode item : history) {
            if (gameId.equals(item.get("id").asText())) {
                containsGame = true;
                break;
            }
        }
        assertThat(containsGame).isTrue();

        JsonNode leaderboard = getJson("/game/leaderboard?n=10", tok1);
        JsonNode entry = findById(leaderboard, p1);
        assertThat(entry).isNotNull();
        assertThat(entry.get("ratio").asDouble()).isEqualTo(1.0);
    }

    @Test
    void computerGameRespondsWithMove() throws Exception {
        register("ai_user");
        String token = login("ai_user");

        JsonNode game = createGame(token, true);
        String gameId = game.get("id").asText();

        game = setCell(game, 0, 0, 1);
        JsonNode result = move(token, gameId, game);

        int oCount = countInGrid(result.get("board").get("grid"), 2);
        assertThat(oCount).isEqualTo(1);
        assertThat(result.get("status").asText()).isEqualTo("IN_PROGRESS");
    }

    @Test
    void invalidMoveIsRejected() throws Exception {
        register("badmove_user");
        String token = login("badmove_user");

        JsonNode game = createGame(token, false);
        String gameId = game.get("id").asText();

        game = setCell(game, 0, 0, 1);
        game = setCell(game, 1, 1, 1);

        mockMvc.perform(post("/game/" + gameId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(game)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unknownGameReturnsNotFound() throws Exception {
        register("notfound_user");
        String token = login("notfound_user");

        mockMvc.perform(get("/game/" + java.util.UUID.randomUUID())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private String register(String login) throws Exception {
        MvcResult res = mockMvc.perform(post("/register").contentType(APPLICATION_JSON)
                        .content("{\"login\":\"" + login + "\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(res.getResponse().getContentAsString(), String.class);
    }

    private String login(String login) throws Exception {
        MvcResult res = mockMvc.perform(post("/login").contentType(APPLICATION_JSON)
                        .content("{\"login\":\"" + login + "\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(res.getResponse().getContentAsString()).get("accessToken").asText();
    }

    private JsonNode createGame(String token, boolean vsComputer) throws Exception {
        MvcResult res = mockMvc.perform(post("/game/create")
                        .param("vsComputer", String.valueOf(vsComputer))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(res.getResponse().getContentAsString());
    }

    private JsonNode move(String token, String gameId, JsonNode game) throws Exception {
        MvcResult res = mockMvc.perform(post("/game/" + gameId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(game)))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(res.getResponse().getContentAsString());
    }

    private JsonNode getJson(String path, String token) throws Exception {
        MvcResult res = mockMvc.perform(get(path).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(res.getResponse().getContentAsString());
    }

    private static JsonNode setCell(JsonNode game, int row, int col, int value) {
        ObjectNode copy = ((ObjectNode) game).deepCopy();
        ArrayNode grid = (ArrayNode) copy.path("board").get("grid");
        ((ArrayNode) grid.get(row)).set(col, IntNode.valueOf(value));
        return copy;
    }

    private static int countInGrid(JsonNode grid, int value) {
        int count = 0;
        for (JsonNode row : grid) {
            for (JsonNode cell : row) {
                if (cell.asInt() == value) {
                    count++;
                }
            }
        }
        return count;
    }

    private static JsonNode findById(JsonNode array, String userId) {
        for (JsonNode node : array) {
            if (userId.equals(node.get("id").asText())) {
                return node;
            }
        }
        return null;
    }
}