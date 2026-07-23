package model.response;

import model.enums.UserType;
import model.enums.UserStatus;

import java.util.UUID;

public class UserDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private UserType userType;
    private UserStatus userStatus;

    public UserDto() {}

    public UserDto(UUID id, String fullName, String email, String phoneNumber, UserType userType, UserStatus userStatus) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.userStatus = userStatus;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public UserType getUserType() { return userType; }
    public void setUserType(UserType userType) { this.userType = userType; }

    public UserStatus getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }
}