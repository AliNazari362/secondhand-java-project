package com.secondhand.service;

import com.secondhand.exception.IllegalTokenException;

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
 */
public class JwtUtil {


    private static final String SECRET = "mySuperSecretKey123!@#";
    private static final String ALGORITHM = "HmacSHA256";

    /**
     * ]
     *
     * <p>Generates a signed JWT token that expires after 24 hours. The token payload
     * contains the user's ID, email, role, issued-at timestamp, expiry timestamp,
     * and a random JWT ID (jti) to prevent replay attacks. Structure: header.payload.signature</p>
     *
     * @param userId the unique identifier of the user (UUID string representation)
     * @param email  the email address of the user
     * @param role   the user's authority/role name (e.g., {@code "USER"}, {@code "ADMIN"})
     * @return a Base64Url-encoded JWT string in {@code header.payload.signature} format
     * @throws RuntimeException if an unexpected cryptographic error occurs during signing
     */
    public static String generateToken(String userId, String email, String role) {
        try {
            // Create Header (Base64)
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(header.getBytes(StandardCharsets.UTF_8));

            // Create Payload (Base64)
            long now = System.currentTimeMillis();
            long exp = now + (24 * 60 * 60 * 1000); // One Day
            String payload = String.format(
                    "{\"userId\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d,\"jti\":\"%s\"}",
                    userId, email, role, now, exp, UUID.randomUUID());
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

            // Create Signature with HMAC-SHA256
            String dataToSign = encodedHeader + "." + encodedPayload;
            String signature = hmacSha256(dataToSign);

            // Combination
            return encodedHeader + "." + encodedPayload + "." + signature;

        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT", e);
        }
    }

    /**
     *
     * <p>Validates a JWT token by checking that it consists of exactly three
     * dot-separated parts and that its HMAC-SHA256 signature matches the recomputed
     * signature over the header and payload.</p>
     *
     * @param token the JWT token string to validate
     * @throws IllegalTokenException if the token fails signature validation
     */
    public static void validateToken(String token) {
        try {

            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new IllegalTokenException("توکن نامعتبر است");

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            // recreate signature
            String dataToSign = header + "." + payload;
            String expectedSignature = hmacSha256(dataToSign);

            if (!signature.equals(expectedSignature)) {
                throw new IllegalTokenException("توکن نامعتبر است");
            }

            if (getExpireDateFromToken(token) < System.currentTimeMillis()) {
                throw new IllegalTokenException("توکن منقضی شده است");
            }
        } catch (Exception e) {
            throw new RuntimeException("فرآیند دریافت توکن به مشکل خورده است");
        }
    }

    private static long getExpireDateFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            return Long.parseLong(extractValue(payloadJson, "exp"));
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     *
     * <p>Validates the token signature first, then extracts the {@code email} claim
     * from the Base64Url-decoded payload JSON. Validation is performed outside the
     * parsing try-catch to ensure {@link IllegalTokenException} is never swallowed.</p>
     *
     * @param token the JWT token string from which to extract the email
     * @return the email address embedded in the token payload
     * @throws IllegalTokenException if the token fails signature validation
     * @throws RuntimeException      if the userId claim cannot be located, is not a valid UUID,
     *                               or any other parsing error occurs
     */
    public static String getEmailFromToken(String token) {
        try {
            validateToken(token);
            String[] parts = token.split("\\.");
            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            // extract email from payload
            return extractValue(payloadJson, "email");

        } catch (IllegalTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("فرآیند دریافت توکن به مشکل خورده است");
        }
    }

    /**
     *
     * <p>Validates the token signature first, then extracts the {@code userId} claim
     * from the Base64Url-decoded payload JSON and parses it as a {@link UUID}.
     * Validation is performed outside the parsing try-catch to ensure
     * {@link IllegalTokenException} is never swallowed.</p>
     *
     * @param token the JWT token string from which to extract the user ID
     * @return the {@link UUID} of the user embedded in the token payload
     * @throws IllegalTokenException if the token fails signature validation
     * @throws RuntimeException      if the userId claim cannot be located, is not a valid UUID,
     *                               or any other parsing error occurs
     */
    public static UUID getUserIdFromToken(String token) {
        try {
            validateToken(token);
            String[] parts = token.split("\\.");
            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            return UUID.fromString(extractValue(payloadJson, "userId"));

        } catch (IllegalTokenException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("فرآیند دریافت توکن به مشکل خورده است");
        }
    }

    /**
     * Computes an HMAC-SHA256 signature for the given data using the specified secret key,
     * and returns the result as a Base64Url-encoded string (without padding).
     *
     * @param data the input string to sign
     * @return the Base64Url-encoded (no-padding) HMAC-SHA256 signature
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
            throw new RuntimeException("Error generating HMAC", e);
        }
    }

    /**
     * Extracts a string value associated with the given key from a simple JSON object string.
     * <p>
     * This is a minimal, library-free JSON field extractor that only supports top-level
     * string values. It does not handle nested objects, arrays, or escaped quotes inside values.
     * </p>
     *
     * @param json the JSON string to search within (e.g., a decoded JWT payload)
     * @param key  the JSON key whose string value should be returned
     * @return the string value associated with the given key
     * @throws RuntimeException if the key is not present in the JSON or the JSON is malformed
     */
    private static String extractValue(String json, String key) {
        // Search in JSON library
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
}
