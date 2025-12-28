package org.example.mematch.infrastructure.web.dto;

import org.example.mematch.domain.entities.User;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String email;

    public AuthResponse() {}

    public AuthResponse(String token, User user) {
        this.token = token;
        this.userId = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

