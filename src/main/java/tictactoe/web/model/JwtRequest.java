package tictactoe.web.model;

import jakarta.validation.constraints.NotBlank;

public class JwtRequest {
    @NotBlank(message = "login must not be blank")
    private String login;

    @NotBlank(message = "password must not be blank")
    private String password;

    public JwtRequest() {}

    public JwtRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
