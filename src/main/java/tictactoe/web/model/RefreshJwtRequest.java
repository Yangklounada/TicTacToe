package tictactoe.web.model;

import jakarta.validation.constraints.NotBlank;

public class RefreshJwtRequest {
    @NotBlank(message = "refreshToken must not be blank")
    private String refreshToken;

    public RefreshJwtRequest() {}

    public RefreshJwtRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}