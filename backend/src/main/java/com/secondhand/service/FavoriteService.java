package com.secondhand.service;

import com.secondhand.dto.adv.AdvSummaryResponse;
import com.secondhand.entity.Adv;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing users' favorite (saved) advertisements.
 * <p>
 * Users can save active or sold advertisements to their favorites list for easy access.
 * Duplicate favorites and removal of non-existent favorites are treated as bad requests.
 * </p>
 */
@Service
public class FavoriteService {

    private final UserService userService;
    private final AdvService advService;

    /**
     * Constructs a {@code FavoriteService} with the required service dependencies.
     *
     * @param userService the service used to look up and persist user entities
     * @param advService  the service used to look up advertisement entities
     */
    public FavoriteService(UserService userService, AdvService advService) {
        this.userService = userService;
        this.advService = advService;
    }

    /**
     * Adds an advertisement to the user's favorites list.
     * <p>
     * Only advertisements in {@link AdvStatus#ACTIVE} or {@link AdvStatus#SOLD} status
     * can be favorited. Adding an already-favorited advertisement is rejected.
     * </p>
     *
     * @param userId the UUID of the user adding the favorite
     * @param advId  the UUID of the advertisement to add to favorites
     * @throws ResourceNotFoundException if no user or advertisement exists with the given ID,
     *                                   or if the advertisement is not in an accessible status
     * @throws BadRequestException       if the advertisement is already in the user's favorites
     */
    public void addFavorite(UUID userId, UUID advId) {
        User user = userService.findUserById(userId);
        Adv adv = advService.findAdvById(advId);

        if (adv.getStatus() != AdvStatus.ACTIVE && adv.getStatus() != AdvStatus.SOLD) {
            throw new ResourceNotFoundException("آگهی در دسترس نیست");
        }

        if (user.getFavorites().contains(adv)) {
            throw new BadRequestException("این علاقه مندی پیش از این ثبت شده است");
        }

        user.addFavorite(adv);
        userService.saveUser(user);
    }

    /**
     * Removes an advertisement from the user's favorites list.
     * <p>
     * Attempting to remove an advertisement that was never favorited is rejected.
     * </p>
     *
     * @param userId the UUID of the user removing the favorite
     * @param advId  the UUID of the advertisement to remove from favorites
     * @throws ResourceNotFoundException if no user or advertisement exists with the given ID
     * @throws BadRequestException       if the advertisement is not currently in the user's favorites
     */
    public void removeFavorite(UUID userId, UUID advId) {
        User user = userService.findUserById(userId);
        Adv adv = advService.findAdvById(advId);

        if (!user.getFavorites().contains(adv)) {
            throw new BadRequestException("این علاقه مندی از ابتدا برای شما ثبت نشده بود");
        }

        user.removeFavorite(adv);
        userService.saveUser(user);
    }

    /**
     * Returns a summary list of all advertisements in the user's favorites.
     *
     * @param userId the UUID of the user whose favorites should be retrieved
     * @return a list of {@link AdvSummaryResponse} objects for the user's favorited advertisements;
     *         never {@code null}, may be empty
     * @throws ResourceNotFoundException if no user exists with the given {@code userId}
     */
    public List<AdvSummaryResponse> getFavorites(UUID userId) {
        User user = userService.findUserById(userId);
        return user.getFavorites().stream()
                .map(advService::toAdvSummaryResponse)
                .collect(Collectors.toList());
    }
}
