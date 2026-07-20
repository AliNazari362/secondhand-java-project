package utils;

import java.util.UUID;

public class SessionManager {

    private static String token;
    private static UUID userId;
    private static String fullName;
    private static String role;

    public static void setSession(String token, UUID userId, String fullName, String role) {
        SessionManager.token = token;
        SessionManager.userId = userId;
        SessionManager.fullName = fullName;
        SessionManager.role = role;
    }

    public static String getToken() { return token; }
    public static UUID getUserId() { return userId; }
    public static String getFullName() { return fullName; }
    public static String getRole() { return role; }

    public static boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    public static boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public static void clear() {
        token = null;
        userId = null;
        fullName = null;
        role = null;
    }
}