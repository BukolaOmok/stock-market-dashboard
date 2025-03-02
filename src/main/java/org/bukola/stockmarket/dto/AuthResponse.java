package org.bukola.stockmarket.dto;

public class AuthResponse {
    private String token;

    // Constructor with token
    public AuthResponse(String token) {
        this.token = token;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}