package utils;

import exception.AuthenticationException;

import java.util.regex.Pattern;

public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(0|\\+98)?9[0-9]{9}$");

    public static void isValidEmail(String email) {
        if(email == null || !EMAIL_PATTERN.matcher(email).matches())
            throw new AuthenticationException("ایمیل وارد شده معتبر نیست");
    }

    public static void isValidPhone(String phone) {
        if(phone == null || phone.isEmpty() || !PHONE_PATTERN.matcher(phone).matches())
            throw new AuthenticationException("شماره تلفن وارد شده معتبر نیست");
    }

    public static void isValidPassword(String password) {
        if(password == null || password.length() < 8)
            throw new AuthenticationException("پسورد وارد شده معتبر نیست");
    }

    public static void isNotEmpty(String... strings) {
        for (String s : strings) {
            if (s == null || s.trim().isEmpty()) {
                throw new AuthenticationException("فیلد نمی تواند خالی باشد");
            }
        }
    }
}