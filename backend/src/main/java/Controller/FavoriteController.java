package Controller;


import DTO.adv.AdvSummaryResponse;
import Service.FavoriteService;
import Service.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public List<AdvSummaryResponse> getFavorites(@RequestHeader("Authorization") String token) {
        return favoriteService.getFavorites(JwtUtil.getUserIdFromToken(token));
    }

    @PostMapping("add-favorite")
    public void addFavorite(@RequestHeader("Authorization") String token, @RequestBody UUID advId) {
        favoriteService.addFavorite(JwtUtil.getUserIdFromToken(token), advId);
    }

    @DeleteMapping("delete-favorite")
    public void removeFavorite(@RequestHeader("Authorization") String token, @RequestBody UUID advId) {
        favoriteService.removeFavorite(JwtUtil.getUserIdFromToken(token), advId);
    }
}
