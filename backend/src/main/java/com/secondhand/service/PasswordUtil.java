package com.secondhand.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for hashing and verifying passwords using SHA-256.
 * <p>
 * Passwords are stored as hexadecimal SHA-256 digests. This is a deterministic,
 * one-way hash with no salt; it is suitable for the current implementation but
 * consider migrating to a salted algorithm (e.g., Bcrypt) for stronger security.
 * </p>
 * <p>
 * All methods are static; this class is not intended to be instantiated.
 * </p>
 */
public class PasswordUtil {

    /**
     * Hashes a plain-text password using the SHA-256 algorithm.
     * <p>
     * The resulting hash is a 64-character lowercase hexadecimal string.
     * </p>
     *
     * @param plainPassword the plain-text password to hash; must not be {@code null}
     * @return a 64-character lowercase hexadecimal SHA-256 hash of the input password
     * @throws RuntimeException if the SHA-256 algorithm is unexpectedly unavailable on this JVM
     */
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing algorithm not found", e);
        }
    }

    /**
     * Verifies a plain-text password against a previously hashed password.
     * <p>
     * Hashes the plain-text input and compares it to the stored hash using
     * {@link String#equals}, which is acceptable here because SHA-256 output
     * is fixed-length and not susceptible to the timing attacks relevant to
     * variable-length comparisons.
     * </p>
     *
     * @param plainPassword  the plain-text password provided by the user
     * @param hashedPassword the stored SHA-256 hash to compare against
     * @return {@code true} if the hash of {@code plainPassword} matches {@code hashedPassword};
     *         {@code false} otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return hashPassword(plainPassword).equals(hashedPassword);
    }
}