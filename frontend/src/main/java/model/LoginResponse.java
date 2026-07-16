package model;

import java.util.UUID;

public class LoginResponse {
    private String token;
    private UUID userId;
    private String fullName;
    private String role; // "USER" یا "ADMIN"

    public LoginResponse() {}

    public LoginResponse(String token, UUID userId, String fullName, String role) {
        this.token = token;
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
    }

    // ---------- Getters & Setters ----------
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}