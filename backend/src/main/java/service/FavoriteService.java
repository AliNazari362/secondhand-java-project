package service;

import dto.adv.AdvSummaryResponse;
import entity.Adv;
import entity.User;
import entity.enums.AdvStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FavoriteService {

    private final UserService userService;
    private final AdvService advService;

    public FavoriteService(UserService userService, AdvService advService) {
        this.userService = userService;
        this.advService = advService;
    }

    public void addFavorite(UUID userId, UUID advId) {
        User user = userService.findUserById(userId);
        Adv adv = advService.findAdvById(advId);

        if (adv.getStatus() != AdvStatus.ACTIVE && adv.getStatus() != AdvStatus.SOLD) {
            throw new RuntimeException("Advertisement is not available");
        }

        if (user.getFavorites().contains(adv)) {
            throw new RuntimeException("Already in favorites");
        }

        user.addFavorite(adv);
        userService.saveUser(user);
    }

    public void removeFavorite(UUID userId, UUID advId) {
        User user = userService.findUserById(userId);
        Adv adv = advService.findAdvById(advId);

        if (!user.getFavorites().contains(adv)) {
            throw new RuntimeException("Not in favorites");
        }

        user.removeFavorite(adv);
        userService.saveUser(user);
    }

    public List<AdvSummaryResponse> getFavorites(UUID userId) {
        User user = userService.findUserById(userId);
        return user.getFavorites().stream()
                .map(advService::toAdvSummaryResponse)
                .collect(Collectors.toList());
    }
}