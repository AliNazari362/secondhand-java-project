package service;

import model.request.LoginRequest;
import model.request.UserRegisterRequest;
import model.response.LoginResponse;
import model.response.UserDto;
import utils.SessionManager;

/**
 * Service class for authentication-related API operations.
 * Handles user login, registration, and logout functionality.
 */
public class AuthService {

    private static final ApiClient api = ApiClient.getInstance();

    /**
     * Authenticates a user with email and password credentials.
     *
     * @param request the login request containing email and password
     * @return the login response with JWT token and user details
     * @throws Exception if the API request fails or credentials are invalid
     */
    public static LoginResponse login(LoginRequest request) throws Exception {
        String response = api.post("/auth/login", request);
        return api.fromJson(response, LoginResponse.class);
    }

    /**
     * Registers a new user account.
     *
     * @param request the registration request with user details
     * @return the created user DTO
     * @throws Exception if the API request fails or validation errors occur
     */
    public static UserDto register(UserRegisterRequest request) throws Exception {
        String response = api.post("/auth/register", request);
        return api.fromJson(response, UserDto.class);
    }

    /**
     * Logs out the current user by clearing the session data.
     */
    public static void logout() {
        SessionManager.clear();
    }
}