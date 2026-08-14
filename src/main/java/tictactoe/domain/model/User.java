package tictactoe.domain.model;

import java.util.List;
import java.util.UUID;

public class User {
    private UUID id;
    private String login;
    private String password;
    private List<Role> roles;

    public User(UUID id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
    }

    public User(UUID id, String login, String password, List<Role> roles) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.roles = roles;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
