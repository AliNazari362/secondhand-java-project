//package service;
//
//import model.request.CommentRequest;
//import model.response.CommentResponseDto;
//
//import java.util.UUID;
//
//public class RatingService {
//
//    public CommentResponseDto rateAdvertisement(UUID advId, CommentRequest request) throws Exception {
//        String json = ApiClient.post("/ratings/" + advId, request);
//        return ApiClient.fromJson(json, CommentResponseDto.class);
//    }
//
//    public double getAverageRating(UUID advId) throws Exception {
//        String json = ApiClient.get("/ratings/" + advId + "/average");
//        return Double.parseDouble(json);
//    }
//
//    public long getRatingCount(UUID advId) throws Exception {
//        String json = ApiClient.get("/ratings/" + advId + "/count");
//        return Long.parseLong(json);
//    }
//}