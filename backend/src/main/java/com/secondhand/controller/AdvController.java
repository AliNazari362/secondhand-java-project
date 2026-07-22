package com.secondhand.controller;

import com.secondhand.dto.adv.*;
import com.secondhand.entity.enums.City;
import com.secondhand.exception.BadRequestException;
import com.secondhand.service.AdvService;
import com.secondhand.service.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for advertisement-related operations.
 *
 * <p>Provides endpoints for browsing active advertisements, viewing advertisement details,
 * retrieving a user's advertisements, and creating, updating, or deleting advertisements.
 * Base path: {@code /api/advs}</p>
 */
@RestController
@RequestMapping("api/advs")
public class AdvController {

    private final AdvService advService;

    /**
     * Constructs an {@code AdvController} with the required service dependency.
     *
     * @param advService the advertisement service used to handle business logic
     */
    public AdvController(AdvService advService) {
        this.advService = advService;
    }

    /**
     * Retrieves a list of active advertisements, optionally filtered by keyword, city, category,
     * price range, and sorted by the specified criteria.
     *
     * @param keyword    an optional search keyword to filter advertisements by title or description;
     *                   pass {@code null} or omit to skip keyword filtering
     * @param city       an optional city name (case-insensitive) to filter advertisements by location;
     *                   must be a valid {@link City} enum name, or {@code null}/blank to skip city filtering
     * @param categoryId an optional category ID to filter advertisements by category;
     *                   pass {@code null} or omit to skip category filtering
     * @param sortBy     an optional sorting criterion; supported values:
     *                   {@code newest} (default), {@code oldest}, {@code priceAsc}, {@code priceDesc}, {@code ratingDesc}
     * @param minPrice   an optional minimum price filter (inclusive, only for products);
     *                   pass {@code null} or omit to skip minimum price filtering
     * @param maxPrice   an optional maximum price filter (inclusive, only for products);
     *                   pass {@code null} or omit to skip maximum price filtering
     * @return a list of {@link AdvSummaryResponse} objects matching the given filters and sorted accordingly
     * @throws BadRequestException if the provided city name does not match any valid {@link City} value
     */
    @GetMapping("search")
    public List<AdvSummaryResponse> getActiveAds(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        City cityEnum = null;
        if (city != null && !city.isBlank()) {
            try {
                cityEnum = City.valueOf(city.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("شهر وارد شده معتبر نیست");
            }
        }
        return advService.getActiveAds(keyword, cityEnum, categoryId, sortBy, minPrice, maxPrice);
    }

    /**
     * Retrieves the full details of a single advertisement by its unique identifier.
     *
     * @param advId the UUID of the advertisement to retrieve
     * @return the {@link AdvDetailResponse} containing all details of the advertisement
     */
    @GetMapping("{advId}")
    public AdvDetailResponse getAdvDetail(@PathVariable UUID advId,
                                          @RequestHeader("Authorization") String token) {
        return advService.getAdvDetail(advId, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Retrieves all advertisements posted by a specific user.
     *
     * @param userId the UUID of the user whose advertisements are to be retrieved
     * @return a list of {@link AdvSummaryResponse} objects belonging to the specified user
     */
    @GetMapping("user/{userId}")
    public List<AdvSummaryResponse> getUserAds(@PathVariable UUID userId) {
        return advService.getUserAds(userId);
    }

    /**
     * Creates a new product advertisement on behalf of the authenticated user.
     *
     * @param token   the JWT bearer token from the {@code Authorization} request header
     * @param request the validated request body containing product advertisement details
     * @return the {@link AdvDetailResponse} of the newly created product advertisement
     */
    @PostMapping("create-product")
    public AdvDetailResponse createProduct(@RequestHeader("Authorization") String token,
                                           @Valid @RequestBody ProductCreateRequest request) {
        return advService.createProduct(request, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Creates a new service advertisement on behalf of the authenticated user.
     *
     * @param token   the JWT bearer token from the {@code Authorization} request header
     * @param request the validated request body containing service advertisement details
     * @return the {@link AdvDetailResponse} of the newly created service advertisement
     */
    @PostMapping("create-service")
    public AdvDetailResponse createService(@RequestHeader("Authorization") String token,
                                           @Valid @RequestBody ServiceCreateRequest request) {
        return advService.createService(request, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Updates an existing product advertisement owned by the authenticated user.
     *
     * @param advId   the UUID of the advertisement to update
     * @param token   the JWT bearer token from the {@code Authorization} request header
     * @param request the validated request body containing updated product advertisement details
     * @return the updated {@link AdvDetailResponse}
     */
    @PutMapping("{advId}/update-product")
    public AdvDetailResponse updateProduct(@PathVariable UUID advId,
                                           @RequestHeader("Authorization") String token,
                                           @Valid @RequestBody ProductUpdateRequest request) {
        return advService.updateProduct(advId, request, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Updates an existing service advertisement owned by the authenticated user.
     *
     * @param advId   the UUID of the advertisement to update
     * @param token   the JWT bearer token from the {@code Authorization} request header
     * @param request the validated request body containing updated service advertisement details
     * @return the updated {@link AdvDetailResponse}
     */
    @PutMapping("{advId}/update-service")
    public AdvDetailResponse updateService(@PathVariable UUID advId,
                                           @RequestHeader("Authorization") String token,
                                           @Valid @RequestBody ServiceUpdateRequest request) {
        return advService.updateService(advId, request, JwtUtil.getUserIdFromToken(token));
    }

    /**
     * Marks an advertisement as sold. Only the owner of the advertisement may perform this action.
     *
     * @param advId the UUID of the advertisement to mark as sold
     * @param token the JWT bearer token from the {@code Authorization} request header
     * @return a success message indicating the advertisement has been marked as sold
     */
    @PutMapping("{advId}/mark-as-sold")
    public ResponseEntity<String> markAsSold(@PathVariable UUID advId,
                                             @RequestHeader("Authorization") String token) {
        advService.markAsSold(advId, JwtUtil.getUserIdFromToken(token));
        return ResponseEntity.ok("وضعیت آگهی با موفقیت به فروخته‌شده تغییر کرد");
    }

    /**
     * Deletes an advertisement. Only the owner of the advertisement may perform this action.
     *
     * @param advId the UUID of the advertisement to delete
     * @param token the JWT bearer token from the {@code Authorization} request header
     * @return a success message indicating the advertisement has been deleted
     */
    @DeleteMapping("{advId}/delete-adv")
    public ResponseEntity<String> deleteAdv(@PathVariable UUID advId,
                                            @RequestHeader("Authorization") String token) {
        advService.deleteAdv(advId, JwtUtil.getUserIdFromToken(token));
        return ResponseEntity.ok("آگهی با موفقیت حذف شد");
    }
}