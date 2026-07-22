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
 * Unit tests for {@link AuthService}.
 * Tests user registration and login functionality.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    private UserRegisterRequest validRegisterRequest;
    private UserLoginRequest validLoginRequest;
    private User testUser;

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

    @Test
    void register_ShouldSucceed_WhenEmailIsNotTaken() {
        // Arrange
        when(userRepository.existsByEmail(validRegisterRequest.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userService.toUserDetailResponse(any(User.class))).thenCallRealMethod();

        // Act
        var response = authService.register(validRegisterRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getEmail(), response.email());
        assertEquals(testUser.getFullName(), response.fullName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailIsAlreadyTaken() {
        // Arrange
        when(userRepository.existsByEmail(validRegisterRequest.email())).thenReturn(true);

        // Act & Assert
        assertThrows(ResourceAlreadyExistsException.class,
                () -> authService.register(validRegisterRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== LOGIN TESTS ====================

    @Test
    void login_ShouldSucceed_WhenCredentialsAreValid() {
        // Arrange
        when(userRepository.findByEmail(validLoginRequest.email()))
                .thenReturn(Optional.of(testUser));

        // Act
        var response = authService.login(validLoginRequest);

        // Assert
        assertNotNull(response);
        assertEquals(testUser.getId(), response.userId());
        assertEquals(testUser.getFullName(), response.fullName());
        assertEquals(testUser.getUserType(), response.role());
        assertNotNull(response.token());
    }

    @Test
    void login_ShouldThrowException_WhenEmailNotFound() {
        // Arrange
        when(userRepository.findByEmail(validLoginRequest.email()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BadRequestException.class,
                () -> authService.login(validLoginRequest));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsIncorrect() {
        // Arrange
        UserLoginRequest invalidPasswordRequest = new UserLoginRequest(
                "hessam@example.com",
                "wrongpassword"
        );
        when(userRepository.findByEmail(invalidPasswordRequest.email()))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(BadRequestException.class,
                () -> authService.login(invalidPasswordRequest));
    }

    @Test
    void login_ShouldThrowException_WhenUserIsBanned() {
        // Arrange
        testUser.setUserStatus(UserStatus.BANNED);
        when(userRepository.findByEmail(validLoginRequest.email()))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(ForbiddenException.class,
                () -> authService.login(validLoginRequest));
    }

    @Test
    void login_ShouldThrowException_WhenUserIsDeleted() {
        // Arrange
        testUser.setUserStatus(UserStatus.DELETED);
        when(userRepository.findByEmail(validLoginRequest.email()))
                .thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(ForbiddenException.class,
                () -> authService.login(validLoginRequest));
    }
}