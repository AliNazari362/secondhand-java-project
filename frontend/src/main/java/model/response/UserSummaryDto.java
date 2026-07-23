package model.response;

import model.enums.UserType;

import java.util.UUID;

public class UserSummaryDto {
    private UUID id;
    private String fullName;
    private String email;
    private UserType userType;

    public UserSummaryDto() {}

    public UserSummaryDto(UUID id, String fullName, String email, UserType userType) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.userType = userType;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }
}