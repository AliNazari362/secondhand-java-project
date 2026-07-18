package Service;

import SpecialException.IllegalTokenException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class JwtUtil {

    private static final String SECRET = "mySuperSecretKey123!@#";
    private static final String ALGORITHM = "HmacSHA256";

    /**
     * تولید توکن JWT ساده با استفاده از JDK خالص
     * ساختار: header.payload.signature
     */
    public static String generateToken(String userId, String email, String role) {
        try {
            // 1. ساخت Header (Base64)
            String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
            String encodedHeader = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(header.getBytes(StandardCharsets.UTF_8));

            // 2. ساخت Payload (Base64)
            long now = System.currentTimeMillis();
            long exp = now + 86400000; // 1 روز
            String payload = String.format(
                    "{\"userId\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"iat\":%d,\"exp\":%d,\"jti\":\"%s\"}",
                    userId, email, role, now, exp, UUID.randomUUID().toString()
            );
            String encodedPayload = Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(payload.getBytes(StandardCharsets.UTF_8));

            // 3. ساخت Signature با HMAC-SHA256
            String dataToSign = encodedHeader + "." + encodedPayload;
            String signature = hmacSha256(dataToSign, SECRET);

            // 4. ترکیب نهایی
            return encodedHeader + "." + encodedPayload + "." + signature;

        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT", e);
        }
    }

    /**
     * اعتبارسنجی توکن
     */
    public static boolean validateToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return false;
            }

            String header = parts[0];
            String payload = parts[1];
            String signature = parts[2];

            // بازسازی امضا و مقایسه
            String dataToSign = header + "." + payload;
            String expectedSignature = hmacSha256(dataToSign, SECRET);

            return signature.equals(expectedSignature);

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * استخراج email از توکن
     */
    public static String getEmailFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (validateToken(token)) {
                throw new RuntimeException("Invalid token");
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            // استخراج email از JSON (ساده، بدون کتابخانه)
            return extractValue(payloadJson, "email");

        } catch (Exception e) {
            throw new RuntimeException("Error extracting email from token", e);
        }
    }

    /**
     * استخراج userId از توکن
     */
    public static UUID getUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (validateToken(token)) {
                throw new IllegalTokenException("توکن نامعتبر است");
            }

            String payloadJson = new String(
                    Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8
            );

            return UUID.fromString(extractValue(payloadJson, "userId"));

        } catch (Exception e) {
            throw new RuntimeException("Error extracting userId from token", e);
        }
    }

    private static String hmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    key.getBytes(StandardCharsets.UTF_8),
                    ALGORITHM
            );
            mac.init(secretKeySpec);
            byte[] result = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating HMAC", e);
        }
    }

    private static String extractValue(String json, String key) {
        // جستجوی ساده در JSON (بدون کتابخانه)
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