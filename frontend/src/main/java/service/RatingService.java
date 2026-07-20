package service;

import model.request.CommentRequest;
import model.response.CommentResponseDto;

public class RatingService {

    private final ApiClient api = ApiClient.getInstance();

    public CommentResponseDto rateAdvertisement(String advId, CommentRequest request) throws Exception {
        String response = api.post("/ratings/" + advId, request);
        return api.fromJson(response, CommentResponseDto.class);
    }

    public double getAverageRating(String advId) throws Exception {
        String response = api.get("/ratings/" + advId + "/average");
        return Double.parseDouble(response);
    }

    public long getRatingCount(String advId) throws Exception {
        String response = api.get("/ratings/" + advId + "/count");
        return Long.parseLong(response);
    }
}
