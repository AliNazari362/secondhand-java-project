/**
 * Unit tests for {@link AuthService}.
 * Tests user registration and login functionality.
 */
package com.secondhand.service;

import com.secondhand.dto.user.UserLoginRequest;
import com.secondhand.dto.user.UserRegisterRequest;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.UserStatus;
import com.secondhand.entity.enums.UserType;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.ForbiddenException;
import com.secondhand.exception.ResourceAlreadyExistsException;
import com.secondhand.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link AuthService}.
 * Verifies the correct behavior of user registration and login operations.
 * Covers success scenarios as well as edge cases such as duplicate emails,
 * invalid credentials, and blocked accounts.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    /** Mocked repository for user data access. */
    @Mock
    private UserRepository userRepository;

    /** Mocked service for user operations. */
    @Mock
    private UserService userService;

    /** The service under test, with mocks injected. */
    @InjectMocks
    private AuthService authService;

    /** Valid registration request fixture. */
    private UserRegisterRequest validRegisterRequest;

    /** Valid login request fixture. */
    private UserLoginRequest validLoginRequest;

    /** Test user instance. */
    private User testUser;

    /**
     * Sets up common test fixtures before each test.
     * Initializes valid registration and login requests and a test user.
     */
    @BeforeEach
    void setUp() {
        validRegisterRequest = new UserRegisterRequest(
                "Hessam Test",
                "hessam@example.com",
                "12345678",
                "+989123456789"
        );

        validLoginRequest = new UserLoginRequest(
                "hessam@example.com",
                "12345678"
        );

        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setEmail("hessam@example.com");
        testUser.setPassword(PasswordUtil.hashPassword("12345678"));
        testUser.setFullName("Hessam Test");
        testUser.setUserType(UserType.USER);
        testUser.setUserStatus(UserStatus.ACTIVE);
        testUser.setPhoneNumber("+989123456789");
    }

    // ==================== REGISTER TESTS ====================

    /**
     * Tests that a user is successfully registered when the email is not already taken.
     * Verifies that the user is saved in the repository and the response is correct.
     */
    @Test
    void register_ShouldSucceed_WhenEmailIsNotTaken() {
        when(userRepository.existsByEmail(validRegisterRequest.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userService.toUserDetailResponse(any(User.class))).thenCallRealMethod();

        var response = authService.register(validRegisterRequest);

        assertNotNull(response);
        assertEquals(testUser.getEmail(), response.email());
        assertEquals(testUser.getFullName(), response.fullName());
        verify(userRepository).save(any(User.class));
    }

    /**
     * Tests that registration fails when the email is already taken.
     * Expects a {@link ResourceAlreadyExistsException}.
     */
    @Test
    void register_ShouldThrowException_WhenEmailIsAlreadyTaken() {
        when(userRepository.existsByEmail(validRegisterRequest.email())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> authService.register(validRegisterRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== LOGIN TESTS ====================

    /**
     * Tests that login succeeds when valid credentials are provided.
     * Verifies that a JWT token is returned in the response.
     */
    @Test
    void login_ShouldSucceed_WhenCredentialsAreValid() {
        when(userRepository.findByEmail(validLoginRequest.email()))
                .thenReturn(Optional.of(testUser));

        var response = authService.login(validLoginRequest);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.userId());
        assertEquals(testUser.getFullName(), response.fullName());
        assertEquals(testUser.getUserType(), response.role());
        assertNotNull(response.token());
    }

    /**
     * Tests that login fails when the email is not found in the system.
     * Expects a {@link BadRequestException}.
     */
    @Test
    void login_ShouldThrowException_WhenEmailNotFound() {
        when(userRepository.findByEmail(validLoginRequest.email()))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class,
                () -> authService.login(validLoginRequest));
    }

    /**
     * Tests that login fails when the password is incorrect.
     * Expects a {@link BadRequestException}.
     */
    @Test
    void login_ShouldThrowException_WhenPasswordIsIncorrect() {
        UserLoginRequest invalidPasswordRequest = new UserLoginRequest(
                "hessam@example.com",
                "wrongpassword"
        );
        when(userRepository.findByEmail(invalidPasswordRequest.email()))
                .thenReturn(Optional.of(testUser));

        assertThrows(BadRequestException.class,
                () -> authService.login(invalidPasswordRequest));
    }

    /**
     * Tests that login fails when the user account is banned.
     * Expects a {@link ForbiddenException}.
     */
    @Test
    void login_ShouldThrowException_WhenUserIsBanned() {
        testUser.setUserStatus(UserStatus.BANNED);
        when(userRepository.findByEmail(validLoginRequest.email()))
                .thenReturn(Optional.of(testUser));

        assertThrows(ForbiddenException.class,
                () -> authService.login(validLoginRequest));
    }

    /**
     * Tests that login fails when the user account is deleted.
     * Expects a {@link ForbiddenException}.
     */
    @Test
    void login_ShouldThrowException_WhenUserIsDeleted() {
        testUser.setUserStatus(UserStatus.DELETED);
        when(userRepository.findByEmail(validLoginRequest.email()))
                .thenReturn(Optional.of(testUser));

        assertThrows(ForbiddenException.class,
                () -> authService.login(validLoginRequest));
    }
}