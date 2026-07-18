package Service;

import DTO.adv.AdvSummaryResponse;
import Entity.Adv;
import Entity.User;
import Entity.enums.AdvStatus;
import SpecialException.AdvertisementIsNotAvailableException;
import SpecialException.IllegalFavoriteException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            throw new AdvertisementIsNotAvailableException("آگهی در دسترس نیست");
        }

        if (user.getFavorites().contains(adv)) {
            throw new IllegalFavoriteException("این علاقه مندی پیش از این ثبت شده است");
        }

        user.addFavorite(adv);
        userService.saveUser(user);
    }

    public void removeFavorite(UUID userId, UUID advId) {
        User user = userService.findUserById(userId);
        Adv adv = advService.findAdvById(advId);

        if (!user.getFavorites().contains(adv)) {
            throw new IllegalFavoriteException("این علاقه مندی از ابتدا برای شما ثبت نشده بود");
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