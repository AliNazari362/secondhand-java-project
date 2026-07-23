package utils;

import java.util.UUID;

/**
 * Manages the current user's session data including authentication token,
 * user ID, full name, and role. Provides methods for checking login state
 * and admin privileges.
 */
public class SessionManager {

    private static String token;
    private static UUID userId;
    private static String fullName;
    private static String role;

    /**
     * Stores the session data for the currently authenticated user.
     *
     * @param token    the JWT authentication token
     * @param userId   the unique identifier of the user
     * @param fullName the display name of the user
     * @param role     the user's role (e.g., "ADMIN" or "USER")
     */
    public static void setSession(String token, UUID userId, String fullName, String role) {
        SessionManager.token = token;
        SessionManager.userId = userId;
        SessionManager.fullName = fullName;
        SessionManager.role = role;
    }

    /**
     * Returns the current JWT authentication token.
     *
     * @return the token, or null if no user is logged in
     */
    public static String getToken() { return token; }

    /**
     * Returns the current user's unique identifier.
     *
     * @return the user ID, or null if no user is logged in
     */
    public static UUID getUserId() { return userId; }

    /**
     * Returns the current user's display name.
     *
     * @return the full name, or null if no user is logged in
     */
    public static String getFullName() { return fullName; }

    /**
     * Returns the current user's role.
     *
     * @return the role string (e.g., "ADMIN", "USER"), or null if no user is logged in
     */
    public static String getRole() { return role; }

    /**
     * Checks whether a user is currently logged in.
     *
     * @return true if a valid token exists, false otherwise
     */
    public static boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    /**
     * Checks whether the current user has admin privileges.
     *
     * @return true if the user's role is "ADMIN", false otherwise
     */
    public static boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * Clears all session data, effectively logging out the user.
     */
    public static void clear() {
        token = null;
        userId = null;
        fullName = null;
        role = null;
    }
}