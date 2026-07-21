package service;

import model.request.UserChangePasswordRequest;
import model.request.UserUpdateRequest;
import model.response.UserDto;
import utils.SessionManager;

/**
 * Service for user-related API calls.
 * Handles profile retrieval, update, password change, and account deletion.
 */
public class UserService {

    private final ApiClient api = ApiClient.getInstance();

    /**
     * Retrieves the current user's profile.
     */
    public UserDto getProfile() throws Exception {
        String response = api.get("/user");
        return api.fromJson(response, UserDto.class);
    }

    /**
     * Updates the current user's profile.
     */
    public UserDto updateProfile(UserUpdateRequest request) throws Exception {
        String response = api.put("/user/update-profile", request);
        return api.fromJson(response, UserDto.class);
    }

    /**
     * Changes the user's password.
     */
    public String changePassword(UserChangePasswordRequest request) throws Exception {
        return api.put("/user/change-password", request);
    }

    /**
     * Soft-deletes the user's account.
     */
    public String deleteAccount() throws Exception {
        return api.delete("/user/delete-account");
    }
}