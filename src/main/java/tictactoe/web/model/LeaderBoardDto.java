package tictactoe.web.model;

import java.util.UUID;

public class LeaderBoardDto {
    private UUID id;
    private String login;
    private double ratio;

    public LeaderBoardDto() {}

    public LeaderBoardDto(UUID id, String login, double ratio) {
        this.id = id;
        this.login = login;
        this.ratio = ratio;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
