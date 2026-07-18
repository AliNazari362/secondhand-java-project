package controller;

import dto.adv.AdvSummaryResponse;
import exception.IllegalTokenException;
import service.FavoriteService;
import service.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<AdvSummaryResponse> getFavorites(@RequestHeader("Authorization") String header) {
        UUID userId = extractUserIdFromToken(header);
        return favoriteService.getFavorites(userId);
    }

    @PostMapping
    public void addFavorite(@RequestHeader("Authorization") String header,
                            @RequestBody AddFavoriteRequest request) {
        UUID userId = extractUserIdFromToken(header);
        favoriteService.addFavorite(userId, request.getAdvId());
    }

    @DeleteMapping("/{advId}")
    public void removeFavorite(@PathVariable UUID advId,
                               @RequestHeader("Authorization") String header) {
        UUID userId = extractUserIdFromToken(header);
        favoriteService.removeFavorite(userId, advId);
    }

    public static class AddFavoriteRequest {
        private UUID advId;
        public UUID getAdvId() { return advId; }
        public void setAdvId(UUID advId) { this.advId = advId; }
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