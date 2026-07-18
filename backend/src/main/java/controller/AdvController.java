package controller;

import dto.adv.*;
import exception.IllegalTokenException;
import service.AdvService;
import service.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/advertisements")
public class AdvController {

    private final AdvService advService;

    public AdvController(AdvService advService) {
        this.advService = advService;
    }

    // ============================================
    // مسیرهای عمومی (بدون توکن)
    // ============================================

    @GetMapping
    public List<AdvSummaryResponse> getActiveAds(@RequestParam(required = false) String keyword,
                                                 @RequestParam(required = false) String city) {
        entity.enums.City cityEnum = city != null ? entity.enums.City.valueOf(city) : null;
        return advService.getActiveAds(keyword, cityEnum);
    }

    @GetMapping("/{id}")
    public AdvDetailResponse getAdvDetail(@PathVariable UUID id) {
        return advService.getAdvDetail(id);
    }

    // ============================================
    // مسیرهای محافظت‌شده (نیاز به توکن)
    // ============================================

    @PostMapping("/product")
    public AdvDetailResponse createProduct(@RequestHeader("Authorization") String header,
                                           @Valid @RequestBody ProductCreateRequest request) {
        UUID userId = extractUserIdFromToken(header);
        return advService.createProduct(request, userId);
    }

    @PostMapping("/service")
    public AdvDetailResponse createService(@RequestHeader("Authorization") String header,
                                           @Valid @RequestBody ServiceCreateRequest request) {
        UUID userId = extractUserIdFromToken(header);
        return advService.createService(request, userId);
    }

    @PutMapping("/product/{id}")
    public AdvDetailResponse updateProduct(@PathVariable UUID id,
                                           @RequestHeader("Authorization") String header,
                                           @Valid @RequestBody ProductUpdateRequest request) {
        UUID userId = extractUserIdFromToken(header);
        return advService.updateProduct(id, request, userId);
    }

    @PutMapping("/service/{id}")
    public AdvDetailResponse updateService(@PathVariable UUID id,
                                           @RequestHeader("Authorization") String header,
                                           @Valid @RequestBody ServiceUpdateRequest request) {
        UUID userId = extractUserIdFromToken(header);
        return advService.updateService(id, request, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteAdv(@PathVariable UUID id,
                          @RequestHeader("Authorization") String header) {
        UUID userId = extractUserIdFromToken(header);
        advService.deleteAdv(id, userId);
    }

    @PutMapping("/{id}/sold")
    public void markAsSold(@PathVariable UUID id,
                           @RequestHeader("Authorization") String header) {
        UUID userId = extractUserIdFromToken(header);
        advService.markAsSold(id, userId);
    }

    @GetMapping("/my-ads")
    public List<AdvSummaryResponse> getUserAds(@RequestHeader("Authorization") String header) {
        UUID userId = extractUserIdFromToken(header);
        return advService.getUserAds(userId);
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