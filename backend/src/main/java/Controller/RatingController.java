package Controller;

import DTO.comment.CommentRequest;
import DTO.comment.CommentResponse;
import Service.JwtUtil;
import Service.RatingService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("{advId}")
    public CommentResponse rateAdvertisement(@PathVariable UUID advId, @RequestHeader("Authorization") String token, @RequestBody CommentRequest request) {
        return ratingService.rateAdvertisement(advId, request, JwtUtil.getUserIdFromToken(token));
    }

    @GetMapping("/{advId}/average")
    public double getAverageRating(@PathVariable UUID advId) {
        return ratingService.getAverageRating(advId);
    }

    @GetMapping("/{advId}/count")
    public long getRatingCount(@PathVariable UUID advId) {
        return ratingService.getRatingCount(advId);
    }

}
