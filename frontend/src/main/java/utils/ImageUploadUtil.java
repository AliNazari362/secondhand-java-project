package utils;

import service.ApiClient;
import exception.ApiException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for uploading images to the backend server.
 * <p>
 * Provides convenience methods for uploading images from various sources:
 * raw byte arrays, local files, and input streams. All upload operations
 * delegate to {@link ApiClient} which handles the multipart/form-data
 * construction and HTTP communication.
 * </p>
 *
 * <p><b>Usage example:</b></p>
 * <pre>{@code
 * // Upload from file path
 * String serverPath = ImageUploadUtil.uploadImageFromFile(Path.of("photo.jpg"));
 *
 * // Upload from byte array
 * byte[] imageData = ...;
 * String serverPath = ImageUploadUtil.uploadImage(imageData, "avatar.png");
 *
 * // Upload from InputStream
 * InputStream stream = file.getInputStream();
 * String serverPath = ImageUploadUtil.uploadImageFromStream(stream, "image.jpg");
 * }</pre>
 *
 */
public class ImageUploadUtil {

    /**
     * Uploads an image to the server using raw binary data.
     *
     * @param imageBytes the binary data of the image to upload, must not be {@code null} or empty
     * @param fileName   the name of the file including its extension
     * @return the server-side path where the uploaded image was stored
     * @throws Exception if the upload fails due to network issues, server errors,
     *                   or invalid input parameters
     * @throws ApiException if the server returns an HTTP error response (4xx or 5xx)
     */
    public static String uploadImage(byte[] imageBytes, String fileName) throws Exception {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("تصویر نمی‌تواند خالی باشد");
        }
        if (fileName == null || fileName.isEmpty()) {
            throw new IllegalArgumentException("نام فایل نمی‌تواند خالی باشد");
        }
        return ApiClient.getInstance().uploadFile(imageBytes, fileName);
    }

    /**
     * Uploads an image from a local file path.
     *
     * @param filePath the path to the local image file
     * @return the server-side path where the uploaded image was stored
     * @throws Exception if the upload fails, the file does not exist, or cannot be read
     */
    public static String uploadImageFromFile(Path filePath) throws Exception {
        byte[] bytes = Files.readAllBytes(filePath);
        String fileName = filePath.getFileName().toString();
        return uploadImage(bytes, fileName);
    }

    /**
     * Uploads an image from an {@link InputStream}.
     *
     * @param inputStream the input stream containing the image data
     * @param fileName    the name to assign to the uploaded file
     * @return the server-side path where the uploaded image was stored
     * @throws Exception if the upload fails or the stream cannot be read
     */
    public static String uploadImageFromStream(InputStream inputStream, String fileName) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        return uploadImage(buffer.toByteArray(), fileName);
    }

    /**
     * Validates if a file is an allowed image type based on its extension.
     *
     * @param fileName the name of the file
     * @return {@code true} if the file has a valid image extension, {@code false} otherwise
     */
    public static boolean isValidImageFile(String fileName) {
        if (fileName == null) return false;
        String lower = fileName.toLowerCase();
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                lower.endsWith(".png") || lower.endsWith(".gif") ||
                lower.endsWith(".bmp") || lower.endsWith(".webp");
    }

    /**
     * Validates the size of an image (max 5MB by default).
     *
     * @param imageBytes the image data
     * @param maxSizeMB  maximum allowed size in megabytes
     * @return {@code true} if the image size is within the limit, {@code false} otherwise
     */
    public static boolean isImageSizeValid(byte[] imageBytes, int maxSizeMB) {
        if (imageBytes == null) return false;
        long maxBytes = (long) maxSizeMB * 1024 * 1024;
        return imageBytes.length <= maxBytes;
    }

    /**
     * Generates a unique filename for uploaded images.
     *
     * @param originalName the original file name
     * @return a unique filename with timestamp and random suffix
     */
    public static String generateUniqueFileName(String originalName) {
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalName.substring(dotIndex);
        }
        return System.currentTimeMillis() + "_" +
                java.util.UUID.randomUUID().toString().substring(0, 8) + extension;
    }
}