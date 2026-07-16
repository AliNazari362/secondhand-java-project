package model;

import java.util.UUID;

public class UserDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String userType;    // "USER" یا "ADMIN"
    private String userStatus;  // "ACTIVE" یا "BANNED"

    public UserDto() {}

    public UserDto(UUID id, String fullName, String email, String phoneNumber, String userType, String userStatus) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.userStatus = userStatus;
    }

    // ---------- Getters & Setters ----------
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public String getUserStatus() { return userStatus; }
    public void setUserStatus(String userStatus) { this.userStatus = userStatus; }
}