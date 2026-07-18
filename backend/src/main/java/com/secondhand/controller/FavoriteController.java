package com.secondhand.controller;


import com.secondhand.dto.adv.AdvSummaryResponse;
import com.secondhand.service.FavoriteService;
import com.secondhand.service.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing a user's favorite advertisements.
 *
 * <p>Provides endpoints for retrieving, adding, and removing favorite advertisements
 * for the authenticated user.
 * Base path: {@code /api/favorites}</p>
 */
@RestController
@RequestMapping("api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * Constructs a {@code FavoriteController} with the required service dependency.
     *
     * @param favoriteService the favorite service used to manage favorite advertisement logic
     */
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    /**
     * Retrieves all advertisements that the authenticated user has marked as favorites.
     *
     * @param token the JWT bearer token from the {@code Authorization} request header
     * @return a list of {@link AdvSummaryResponse} objects representing the user's favorites
     */
    @GetMapping
    public List<AdvSummaryResponse> getFavorites(@RequestHeader("Authorization") String token) {
        return favoriteService.getFavorites(JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Adds an advertisement to the authenticated user's favorites list.
     *
     * @param token the JWT bearer token from the {@code Authorization} request header
     * @param advId the UUID of the advertisement to add to favorites
     */
    @PostMapping("add-favorite")
    public void addFavorite(@RequestHeader("Authorization") String token, @RequestBody UUID advId) {
        favoriteService.addFavorite(JwtUtil.getUserIdFromToken(token), advId);
    }

    /**
     * Removes an advertisement from the authenticated user's favorites list.
     *
     * @param token the JWT bearer token from the {@code Authorization} request header
     * @param advId the UUID of the advertisement to remove from favorites
     */
    @DeleteMapping("delete-favorite")
    public void removeFavorite(@RequestHeader("Authorization") String token, @RequestBody UUID advId) {
        favoriteService.removeFavorite(JwtUtil.getUserIdFromToken(token), advId);
    }
}
