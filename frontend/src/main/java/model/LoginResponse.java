package model;

import model.enums.UserRole;

import java.util.UUID;

public class LoginResponse {
    private String token;
    private UUID userId;
    private String fullName;
    private UserRole role;

    public LoginResponse() {}

    public LoginResponse(String token, UUID userId, String fullName, UserRole role) {
        this.token = token;
        this.userId = userId;
        this.fullName = fullName;
        this.role = role;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}