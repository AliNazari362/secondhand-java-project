package controller;

import dto.comment.CommentRequest;
import dto.comment.CommentResponse;
import exception.IllegalTokenException;
import service.RatingService;
import service.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/{advId}")
    public CommentResponse rateAdvertisement(@PathVariable UUID advId,
                                             @RequestHeader("Authorization") String header,
                                             @Valid @RequestBody CommentRequest request) {
        UUID userId = extractUserIdFromToken(header);
        return ratingService.rateAdvertisement(advId, request, userId);
    }

    @GetMapping("/{advId}/average")
    public RatingSummaryResponse getAverageRating(@PathVariable UUID advId) {
        double avg = ratingService.getAverageRating(advId);
        long count = ratingService.getRatingCount(advId);
        return new RatingSummaryResponse(avg, count);
    }

    public static class RatingSummaryResponse {
        private final double average;
        private final long count;
        public RatingSummaryResponse(double average, long count) {
            this.average = average;
            this.count = count;
        }
        public double getAverage() { return average; }
        public long getCount() { return count; }
    }

    private UUID extractUserIdFromToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalTokenException("توکن نامعتبر است");
        }
        String token = header.substring(7);
        if (!JwtUtil.validateToken(token)) {
            throw new IllegalTokenException("توکن نامعتبر یا منقضی شده است");
        }
        String userIdStr = JwtUtil.getUserIdFromToken(token);
        return UUID.fromString(userIdStr);
    }
}