/**
 * Unit tests for {@link FavoriteService}.
 * Tests adding, removing, and retrieving favorite advertisements for a user.
 */
package com.secondhand.service;

import com.secondhand.dto.adv.AdvSummaryResponse;
import com.secondhand.entity.Adv;
import com.secondhand.entity.Product;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.AdvStatus;
import com.secondhand.entity.enums.AdvType;
import com.secondhand.entity.enums.City;
import com.secondhand.entity.enums.UserType;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link FavoriteService}.
 * Verifies the correct behavior of favorite-related operations including
 * adding, removing, and retrieving a user's favorite advertisements.
 */
@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    /** Mocked service for user operations. */
    @Mock
    private UserService userService;

    /** Mocked service for advertisement operations. */
    @Mock
    private AdvService advService;

    /** The service under test, with mocks injected. */
    @InjectMocks
    private FavoriteService favoriteService;

    // ==================== TEST FIXTURES ====================

    /** User ID. */
    private UUID userId;

    /** Advertisement ID. */
    private UUID advId;

    /** Test user instance. */
    private User testUser;

    /** Test advertisement. */
    private Adv testAdv;

    /** Seller user instance. */
    private User seller;

    /**
     * Sets up common test fixtures before each test.
     */
    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        advId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setFullName("Hessam Test");
        testUser.setEmail("hessam@example.com");
        testUser.setUserType(UserType.USER);
        testUser.setFavorites(new ArrayList<>());

        seller = new User();
        seller.setId(UUID.randomUUID());
        seller.setFullName("Seller User");
        seller.setEmail("seller@example.com");
        seller.setUserType(UserType.USER);

        testAdv = new Product();
        testAdv.setId(advId);
        testAdv.setFullName("Test Product");
        testAdv.setStatus(AdvStatus.ACTIVE);
        testAdv.setAdvType(AdvType.PRODUCT);
        testAdv.setCity(City.TEHRAN);
        testAdv.setUser(seller);
        testAdv.setCreationDate(LocalDateTime.now());
        ((Product) testAdv).setPrice(BigDecimal.valueOf(1000000));
    }

    // ==================== ADD FAVORITE TESTS ====================

    /**
     * Tests that adding an advertisement to favorites succeeds when all conditions are valid.
     * Verifies that the advertisement is added to the user's favorites list and saved.
     */
    @Test
    void addFavorite_ShouldSucceed_WhenValid() {
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        favoriteService.addFavorite(userId, advId);

        assertTrue(testUser.getFavorites().contains(testAdv));
        verify(userService).saveUser(testUser);
    }

    /**
     * Tests that adding a favorite fails when the advertisement is not active or sold.
     * Expects a {@link ResourceNotFoundException}.
     */
    @Test
    void addFavorite_ShouldThrowException_WhenAdvertisementNotActiveOrSold() {
        testAdv.setStatus(AdvStatus.PENDING);
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        assertThrows(ResourceNotFoundException.class,
                () -> favoriteService.addFavorite(userId, advId));
    }

    /**
     * Tests that adding a favorite fails when the advertisement is already favorited.
     * Expects a {@link BadRequestException}.
     */
    @Test
    void addFavorite_ShouldThrowException_WhenAlreadyFavorited() {
        testUser.getFavorites().add(testAdv);
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        assertThrows(BadRequestException.class,
                () -> favoriteService.addFavorite(userId, advId));
    }

    // ==================== REMOVE FAVORITE TESTS ====================

    /**
     * Tests that removing an advertisement from favorites succeeds when it is favorited.
     */
    @Test
    void removeFavorite_ShouldSucceed_WhenExists() {
        testUser.getFavorites().add(testAdv);
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        favoriteService.removeFavorite(userId, advId);

        assertFalse(testUser.getFavorites().contains(testAdv));
        verify(userService).saveUser(testUser);
    }

    /**
     * Tests that removing a favorite fails when the advertisement is not favorited.
     * Expects a {@link BadRequestException}.
     */
    @Test
    void removeFavorite_ShouldThrowException_WhenNotFavorited() {
        when(userService.findUserById(userId)).thenReturn(testUser);
        when(advService.findAdvById(advId)).thenReturn(testAdv);

        assertThrows(BadRequestException.class,
                () -> favoriteService.removeFavorite(userId, advId));
    }

    // ==================== GET FAVORITES TESTS ====================

    /**
     * Tests that retrieving a user's favorites returns a list of their favorited advertisements.
     */
    @Test
    void getFavorites_ShouldReturnList() {
        testUser.getFavorites().add(testAdv);
        when(userService.findUserById(userId)).thenReturn(testUser);

        AdvSummaryResponse mockResponse = new AdvSummaryResponse(
                testAdv.getId(),
                testAdv.getFullName(),
                testAdv.getAdvType(),
                testAdv.getStatus(),
                testAdv.getCity(),
                seller.getFullName(),
                seller.getId(),
                testAdv.getCreationDate(),
                "image.jpg",
                "Electronics",
                ((Product) testAdv).getPrice()
        );
        when(advService.toAdvSummaryResponse(any(Adv.class))).thenReturn(mockResponse);

        var results = favoriteService.getFavorites(userId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testAdv.getFullName(), results.get(0).fullName());
        assertEquals(seller.getFullName(), results.get(0).ownerFullName());
    }
}