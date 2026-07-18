package com.secondhand.controller;


import com.secondhand.dto.adv.*;
import com.secondhand.entity.enums.City;
import com.secondhand.service.AdvService;
import com.secondhand.service.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/advs")
public class AdvController {

    private final AdvService advService;

    public AdvController(AdvService advService) {
        this.advService = advService;
    }

    @GetMapping("{city}")
    public List<AdvSummaryResponse> getActiveAds(@PathVariable String city, @RequestBody String keyword) {
        return advService.getActiveAds(keyword, City.valueOf(city.toUpperCase()));
    }

    @GetMapping("{advId}")
    public AdvDetailResponse getAdvDetail(@PathVariable UUID advId) {
        return advService.getAdvDetail(advId);
    }

    @GetMapping("{userId}")
    public List<AdvSummaryResponse> getUserAds(@PathVariable UUID userId) {
        return advService.getUserAds(userId);
    }

    @PostMapping("create-product")
    public AdvDetailResponse createProduct(@RequestHeader("Authorization") String token, @RequestBody ProductCreateRequest request) {
        return advService.createProduct(request, JwtUtil.getUserIdFromToken(token));
    }

    @PostMapping("create-service")
    public AdvDetailResponse createService(@RequestHeader("Authorization") String token, @RequestBody ServiceCreateRequest request) {
        return advService.createService(request, JwtUtil.getUserIdFromToken(token));
    }

    @PutMapping("{advId}/update-product")
    public AdvDetailResponse updateProduct(@PathVariable UUID advId, @RequestHeader("Authorization") String token, @RequestBody ProductUpdateRequest request) {
        return advService.updateProduct(advId, request, JwtUtil.getUserIdFromToken(token));
    }

    @PutMapping("{advId}/update-service")
    public AdvDetailResponse updateService(@PathVariable UUID advId, @RequestHeader("Authorization") String token, @RequestBody ServiceUpdateRequest request) {
        return advService.updateService(advId, request, JwtUtil.getUserIdFromToken(token));
    }

    @PutMapping("{advId}/mark-as-sold")
    public void markAsSold(@PathVariable UUID advId, @RequestHeader("Authorization") String token) {
        advService.markAsSold(advId, JwtUtil.getUserIdFromToken(token));
    }

    @DeleteMapping("{advId}/delete-adv")
    public void deleteAdv(@PathVariable UUID advId, @RequestHeader("Authorization") String token) {
        advService.deleteAdv(advId, JwtUtil.getUserIdFromToken(token));
    }
}
