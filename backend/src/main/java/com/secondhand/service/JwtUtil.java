package com.secondhand.service;

import com.secondhand.exception.IllegalTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

/**
 * Utility class for generating, validating, and parsing JSON Web Tokens (JWTs).
 * <p>
 * Implements a minimal, dependency-free JWT mechanism using pure JDK cryptography
 * (HMAC-SHA256). Tokens follow the standard {@code header.payload.signature} structure
 * where each part is Base64Url-encoded (without padding).
 * </p>
 * <p>
 * Generated tokens are valid for 24 hours and contain the following claims:
 * {@code userId}, {@code email}, {@code role}, {@code iat} (issued-at),
 * {@code exp} (expiry), and {@code jti} (unique token ID).
 * </p>
 * <p>
 * <strong>All public methods accept both raw tokens and full Authorization header values
 * (with the {@code "Bearer "} prefix). The prefix is automatically stripped before processing.</strong>
 * </p>
 */
public class JwtUtil {

    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    private static final String SECRET = "mySuperSecretKey123!@#";
    private static final String ALGORITHM = "HmacSHA256";

    /**
     * Strips the {@code "Bearer "} prefix from the given token string if present.
     *
     * @param tokenOrHeader the raw token or Authorization header value
     * @return the token without the prefix, or the original if no prefix
     */
    private static String cleanToken(String tokenOrHeader) {
        if (tokenOrHeader != null && tokenOrHeader.startsWith("Bearer ")) {
            return tokenOrHeader.substring(7);
        }
        return tokenOrHeader;
    }

    /**
     * Generates a signed JWT token that expires after 24 hours.
     *
     * @param userId the unique identifier of the user (UUID string representation)
     * @param email  the email address of the user
     * @param role   the user's authority/role name (e.g., {@code "USER"}, {@code "ADMIN"})
     * @return a Base64Url-encoded JWT string in {@code header.payload.signature} format
     * @throws RuntimeException if an unexpected cryptographic error occurs during signing
     */
    public static String generateToken(String userId, String email, String role) {
        try {
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(header.getBytes(StandardCharsets.UTF_8));

            long now = System.currentTimeMillis();
            long exp = now + (24 * 60 * 60 * 1000);
            String payload = String.format(
                    "{\"userId\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d,\"jti\":\"%s\"}",
                    userId, email, role, now, exp, UUID.randomUUID());
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

            String dataToSign = encodedHeader + "." + encodedPayload;
            String signature = hmacSha256(dataToSign);

            return encodedHeader + "." + encodedPayload + "." + signature;

        } catch (Exception e) {
            log.error("Error generating JWT", e);
            throw new RuntimeException("Error generating JWT", e);
        }
    }

    /**
     * Validates a JWT token by checking its signature and expiry.
     * Accepts both raw tokens and full Authorization header values (with "Bearer ").
     *
     * @param tokenOrHeader the JWT token or full Authorization header value
     * @throws IllegalTokenException if the token is malformed, invalid, or expired
     */
    public static void validateToken(String tokenOrHeader) {
        String token = cleanToken(tokenOrHeader);
        try {
            if (token == null || token.isEmpty()) {
                throw new IllegalTokenException("توکن خالی است");
            }
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalTokenException("توکن نامعتبر است (تعداد بخش‌ها نامناسب)");
            }

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            String dataToSign = header + "." + payload;
            String expectedSignature = hmacSha256(dataToSign);

            if (!signature.equals(expectedSignature)) {
                throw new IllegalTokenException("توکن نامعتبر است (امضا نادرست)");
            }

            if (getExpireDateFromToken(token) < System.currentTimeMillis()) {
                throw new IllegalTokenException("توکن منقضی شده است");
            }
        } catch (IllegalTokenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token validation", e);
            throw new RuntimeException("فرآیند دریافت توکن به مشکل خورده است: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the expiry timestamp (in milliseconds) from the token payload.
     *
     * @param token the raw JWT token (without "Bearer " prefix)
     * @return the expiry timestamp as a {@code long}
     * @throws RuntimeException if the token is malformed or the "exp" claim is missing
     */
    private static long getExpireDateFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );
            return Long.parseLong(extractNumericValue(payloadJson, "exp"));
        } catch (Exception e) {
            log.error("Error extracting expiry date from token", e);
            throw new RuntimeException("فرآیند دریافت توکن به مشکل خورده است", e);
        }
    }

    /**
     * Extracts the email claim from the token after validating its signature.
     * Accepts both raw tokens and full Authorization header values (with "Bearer ").
     *
     * @param tokenOrHeader the JWT token or full Authorization header value
     * @return the email address embedded in the token
     * @throws IllegalTokenException if the token is invalid or expired
     * @throws RuntimeException      if the email claim cannot be parsed
     */
    public static String getEmailFromToken(String tokenOrHeader) {
        String token = cleanToken(tokenOrHeader);
        try {
            validateToken(token);
            String[] parts = token.split("\\.");
            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );
            return extractStringValue(payloadJson, "email");
        } catch (IllegalTokenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting email from token", e);
            throw new RuntimeException("فرآیند دریافت توکن به مشکل خورده است: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts the user ID (as {@link UUID}) from the token after validating its signature.
     * Accepts both raw tokens and full Authorization header values (with "Bearer ").
     *
     * @param tokenOrHeader the JWT token or full Authorization header value
     * @return the {@link UUID} of the user embedded in the token
     * @throws IllegalTokenException if the token is invalid or expired
     * @throws RuntimeException      if the userId claim cannot be parsed as a valid UUID
     */
    public static UUID getUserIdFromToken(String tokenOrHeader) {
        String token = cleanToken(tokenOrHeader);
        try {
            validateToken(token);
            String[] parts = token.split("\\.");
            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );
            return UUID.fromString(extractStringValue(payloadJson, "userId"));
        } catch (IllegalTokenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error extracting userId from token", e);
            throw new RuntimeException("فرآیند دریافت توکن به مشکل خورده است: " + e.getMessage(), e);
        }
    }

    /**
     * Extracts a string value associated with the given key from a simple JSON object string.
     * The value must be enclosed in double quotes.
     *
     * @param json the JSON string to search within
     * @param key  the JSON key whose string value should be returned
     * @return the string value associated with the given key
     * @throws RuntimeException if the key is not present or the JSON is malformed
     */
    private static String extractStringValue(String json, String key) {
        String searchKey = "\"" + key + "\":\"";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            throw new RuntimeException("Key not found in token: " + key);
        }
        startIndex += searchKey.length();
        int endIndex = json.indexOf("\"", startIndex);
        if (endIndex == -1) {
            throw new RuntimeException("Invalid JSON in token");
        }
        return json.substring(startIndex, endIndex);
    }

    /**
     * Extracts a numeric value (as a string) associated with the given key from a simple JSON object.
     * The value must be a number (not enclosed in quotes).
     *
     * @param json the JSON string to search within
     * @param key  the JSON key whose numeric value should be returned
     * @return the numeric value as a string
     * @throws RuntimeException if the key is not present or the value is not numeric
     */
    private static String extractNumericValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            throw new RuntimeException("Key not found in token: " + key);
        }
        startIndex += searchKey.length();
        int endIndex = startIndex;
        while (endIndex < json.length() && (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '-')) {
            endIndex++;
        }
        if (endIndex == startIndex) {
            throw new RuntimeException("No numeric value found for key: " + key);
        }
        return json.substring(startIndex, endIndex);
    }

    /**
     * Computes an HMAC-SHA256 signature for the given data using the secret key,
     * and returns the result as a Base64Url-encoded string (without padding).
     *
     * @param data the input string to sign
     * @return the Base64Url-encoded HMAC-SHA256 signature
     * @throws RuntimeException if the HMAC algorithm is unavailable or the key is invalid
     */
    private static String hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    SECRET.getBytes(StandardCharsets.UTF_8),
                    ALGORITHM);
            mac.init(secretKeySpec);
            byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error generating HMAC", e);
            throw new RuntimeException("Error generating HMAC", e);
        }
    }
}