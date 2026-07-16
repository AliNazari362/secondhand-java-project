package service;

import java.util.UUID;

/**
 * مدیریت وضعیت کاربر در طول اجرای برنامه.
 * تمام اطلاعات مربوط به کاربر واردشده در اینجا نگهداری می‌شود.
 */
public class SessionManager {

    private static String token;
    private static UUID userId;
    private static String fullName;
    private static String role; // "USER" یا "ADMIN"

    /**
     * تنظیم جلسه‌ی کاربر پس از ورود موفق.
     */
    public static void setSession(String token, UUID userId, String fullName, String role) {
        SessionManager.token = token;
        SessionManager.userId = userId;
        SessionManager.fullName = fullName;
        SessionManager.role = role;
    }

    public static String getToken() {
        return token;
    }

    public static UUID getUserId() {
        return userId;
    }

    public static String getFullName() {
        return fullName;
    }

    public static String getRole() {
        return role;
    }

    /**
     * بررسی می‌کند که آیا کاربر وارد سیستم شده است یا خیر.
     */
    public static boolean isLoggedIn() {
        return token != null && !token.isEmpty();
    }

    /**
     * بررسی می‌کند که آیا کاربر نقش ادمین دارد یا خیر.
     */
    public static boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    /**
     * پاک کردن اطلاعات جلسه (خروج از حساب).
     */
    public static void clear() {
        token = null;
        userId = null;
        fullName = null;
        role = null;
    }
}