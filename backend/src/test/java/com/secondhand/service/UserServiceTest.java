package com.secondhand.service;

import com.secondhand.dto.user.UserChangePasswordRequest;
import com.secondhand.dto.user.UserUpdateRequest;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.UserStatus;
import com.secondhand.entity.enums.UserType;
import com.secondhand.exception.BadRequestException;
import com.secondhand.exception.ResourceAlreadyExistsException;
import com.secondhand.exception.ResourceNotFoundException;
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

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private User testUser;
    private UserUpdateRequest validUpdateRequest;
    private UserChangePasswordRequest validPasswordRequest;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("hessam@example.com");
        testUser.setPassword(PasswordUtil.hashPassword("12345678"));
        testUser.setFullName("Hessam Test");
        testUser.setUserType(UserType.USER);
        testUser.setUserStatus(UserStatus.ACTIVE);
        testUser.setPhoneNumber("+989123456789");

        validUpdateRequest = new UserUpdateRequest(
                "Hessam Updated",
                "newemail@example.com",
                "+989876543210"
        );

        validPasswordRequest = new UserChangePasswordRequest(
                "12345678",
                "newpassword123"
        );
    }

    // ==================== FIND USER TESTS ====================

    @Test
    void findUserById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        User found = userService.findUserById(userId);

        assertNotNull(found);
        assertEquals(testUser.getId(), found.getId());
    }

    @Test
    void findUserById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.findUserById(userId));
    }

    // ==================== PROFILE TESTS ====================

    @Test
    void getProfile_ShouldReturnUserDetailResponse_WhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        var response = userService.getProfile(userId);

        assertNotNull(response);
        assertEquals(testUser.getId(), response.id());
        assertEquals(testUser.getFullName(), response.fullName());
        assertEquals(testUser.getEmail(), response.email());
    }

    @Test
    void updateProfile_ShouldUpdateAllFields_WhenProvided() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userRepository.existsByEmail(validUpdateRequest.email())).thenReturn(false);

        var response = userService.updateProfile(userId, validUpdateRequest);

        assertNotNull(response);
        assertEquals(validUpdateRequest.fullName(), testUser.getFullName());
        assertEquals(validUpdateRequest.email(), testUser.getEmail());
        assertEquals(validUpdateRequest.phoneNumber(), testUser.getPhoneNumber());
    }

    @Test
    void updateProfile_ShouldThrowException_WhenEmailIsTaken() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(validUpdateRequest.email())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> userService.updateProfile(userId, validUpdateRequest));
    }

    @Test
    void updateProfile_ShouldNotThrowException_WhenSameEmail() {
        UserUpdateRequest sameEmailRequest = new UserUpdateRequest(
                "New Name",
                "hessam@example.com",
                null
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        assertDoesNotThrow(() -> userService.updateProfile(userId, sameEmailRequest));
    }

    // ==================== PASSWORD TESTS ====================

    @Test
    void changePassword_ShouldSucceed_WhenCurrentPasswordIsCorrect() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.changePassword(userId, validPasswordRequest);

        verify(userRepository).save(testUser);
        assertNotEquals(PasswordUtil.hashPassword("12345678"), testUser.getPassword());
    }

    @Test
    void changePassword_ShouldThrowException_WhenCurrentPasswordIsWrong() {
        UserChangePasswordRequest wrongRequest = new UserChangePasswordRequest(
                "wrongpassword",
                "newpassword123"
        );
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertThrows(BadRequestException.class,
                () -> userService.changePassword(userId, wrongRequest));
    }

    // ==================== DELETE TESTS ====================

    @Test
    void deleteProfile_ShouldSetStatusToDeleted() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deleteProfile(userId);

        assertEquals(UserStatus.DELETED, testUser.getUserStatus());
        verify(userRepository).save(testUser);
    }
}