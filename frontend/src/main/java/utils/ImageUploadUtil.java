//package utils;
//
//import service.ApiClient;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//
//public class ImageUploadUtil {
//
//    /**
//     * آپلود تصویر با استفاده از ApiClient و ارسال multipart/form-data
//     * @param imageBytes داده‌های باینری تصویر
//     * @param fileName نام فایل (با پسوند)
//     * @return مسیر ذخیره‌شده در سرور
//     */
//    public static String uploadImage(byte[] imageBytes, String fileName) throws Exception {
//        return ApiClient.uploadFile("/images/upload", imageBytes, fileName);
//    }
//
//    /**
//     * آپلود تصویر از یک فایل محلی
//     */
//    public static String uploadImageFromFile(Path filePath) throws Exception {
//        byte[] bytes = Files.readAllBytes(filePath);
//        String fileName = filePath.getFileName().toString();
//        return uploadImage(bytes, fileName);
//    }
//
//    /**
//     * آپلود تصویر از InputStream (مثلاً از انتخاب فایل)
//     */
//    public static String uploadImageFromStream(InputStream inputStream, String fileName) throws Exception {
//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//        byte[] data = new byte[8192];
//        int nRead;
//        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
//            buffer.write(data, 0, nRead);
//        }
//        buffer.flush();
//        return uploadImage(buffer.toByteArray(), fileName);
//    }
//}