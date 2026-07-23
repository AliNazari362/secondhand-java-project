package service;

import model.request.UserChangePasswordRequest;
import model.request.UserUpdateRequest;
import model.response.UserDto;

/**
 * Service class for user-related API operations.
 * Handles profile retrieval, update, password change, and account deletion.
 */
public class UserService {

    private static final ApiClient api = ApiClient.getInstance();

    /**
     * Retrieves the current user's profile information.
     *
     * @return the user DTO with profile details
     * @throws Exception if the API request fails
     */
    public static UserDto getProfile() throws Exception {
        String response = api.get("/user");
        return api.fromJson(response, UserDto.class);
    }

    /**
     * Updates the current user's profile information.
     *
     * @param request the update request with new profile values
     * @return the updated user DTO
     * @throws Exception if the API request fails
     */
    public static UserDto updateProfile(UserUpdateRequest request) throws Exception {
        String response = api.put("/user/update-profile", request);
        return api.fromJson(response, UserDto.class);
    }

    /**
     * Changes the current user's password.
     *
     * @param request the password change request with current and new passwords
     * @return the API response string
     * @throws Exception if the API request fails
     */
    public static String changePassword(UserChangePasswordRequest request) throws Exception {
        return api.put("/user/change-password", request);
    }

    /**
     * Soft-deletes the current user's account.
     *
     * @return the API response string
     * @throws Exception if the API request fails
     */
    public static String deleteAccount() throws Exception {
        return api.delete("/user/delete-account");
    }
}