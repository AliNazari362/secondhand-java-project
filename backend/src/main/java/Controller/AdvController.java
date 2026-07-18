package Controller;


import DTO.adv.*;
import Entity.enums.City;
import Service.AdvService;
import Service.JwtUtil;
import SpecialException.IllegalTokenException;
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
        return advService.createProduct(request, getIdFromToken(token));
    }

    @PostMapping("create-service")
    public AdvDetailResponse createService(@RequestHeader("Authorization") String token, @RequestBody ServiceCreateRequest request) {
        return advService.createService(request, getIdFromToken(token));
    }

    @PutMapping("{advId}/update-product")
    public AdvDetailResponse updateProduct(@PathVariable UUID advId, @RequestHeader("Authorization") String token, @RequestBody ProductUpdateRequest request) {
        return advService.updateProduct(advId, request, getIdFromToken(token));
    }

    @PutMapping("{advId}/update-service")
    public AdvDetailResponse updateService(@PathVariable UUID advId, @RequestHeader("Authorization") String token, @RequestBody ServiceUpdateRequest request) {
        return advService.updateService(advId, request, getIdFromToken(token));
    }

    @PutMapping("{advId}/mark-as-sold")
    public void markAsSold(@PathVariable UUID advId, @RequestHeader("Authorization") String token) {
        advService.markAsSold(advId, getIdFromToken(token));
    }

    @DeleteMapping("{advId}/delete-adv")
    public void deleteAdv(@PathVariable UUID advId, @RequestHeader("Authorization") String token) {
        advService.deleteAdv(advId, getIdFromToken(token));
    }

    private UUID getIdFromToken(String token) {
        if (!JwtUtil.validateToken(token)) {
            throw new IllegalTokenException("درخواست نامعتبر است");
        }
        return UUID.fromString(JwtUtil.getUserIdFromToken(token));
    }
}
