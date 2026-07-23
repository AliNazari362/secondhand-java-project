package utils;

import exception.AuthenticationException;

import java.util.regex.Pattern;

/**
 * Utility class for validating user input fields.
 * Provides methods for email, phone, password, and general non-empty validation.
 * Throws AuthenticationException on validation failure.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(0|\\+98)?9[0-9]{9}$");

    /**
     * Validates that the given string is a properly formatted email address.
     *
     * @param email the email string to validate
     * @throws AuthenticationException if the email is null or invalid
     */
    public static void isValidEmail(String email) {
        if(email == null || !EMAIL_PATTERN.matcher(email).matches())
            throw new AuthenticationException("ایمیل وارد شده معتبر نیست");
    }

    /**
     * Validates that the given string is a properly formatted Iranian phone number.
     *
     * @param phone the phone number string to validate
     * @throws AuthenticationException if the phone number is null, empty, or invalid
     */
    public static void isValidPhone(String phone) {
        if(phone == null || phone.isEmpty() || !PHONE_PATTERN.matcher(phone).matches())
            throw new AuthenticationException("شماره تلفن وارد شده معتبر نیست");
    }

    /**
     * Validates that the given password meets minimum length requirements.
     *
     * @param password the password string to validate
     * @throws AuthenticationException if the password is null or shorter than 8 characters
     */
    public static void isValidPassword(String password) {
        if(password == null || password.length() < 8)
            throw new AuthenticationException("پسورد وارد شده معتبر نیست");
    }

    /**
     * Validates that none of the given strings are null or empty.
     *
     * @param strings one or more strings to check
     * @throws AuthenticationException if any string is null or blank
     */
    public static void isNotEmpty(String... strings) {
        for (String s : strings) {
            if (s == null || s.trim().isEmpty()) {
                throw new AuthenticationException("فیلد نمی تواند خالی باشد");
            }
        }
    }
}