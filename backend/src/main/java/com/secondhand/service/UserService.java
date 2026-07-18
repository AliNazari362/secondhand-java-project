package com.secondhand.service;

import com.secondhand.dto.user.*;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.UserStatus;
import com.secondhand.repository.UserRepository;
import com.secondhand.exception.EmailAlreadyExistException;
import com.secondhand.exception.PasswordIsNotCorrectException;
import com.secondhand.exception.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetailResponse getProfile(UUID userId) {
        User user = findUserById(userId);
        return toUserDetailResponse(user);
    }

    public UserDetailResponse updateProfile(UUID userId, UserUpdateRequest request) {
        User user = findUserById(userId);

        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.email() != null) {
            if (userRepository.existsByEmail(request.email()) && !user.getEmail().equals(request.email())) {
                throw new EmailAlreadyExistException("این ایمیل پیش از این در سیستم ثبت شده است");
            }
            user.setEmail(request.email());
        }
        if (request.phoneNumber() != null) {
            user.setPhoneNumber(request.phoneNumber());
        }

        User updated = userRepository.save(user);
        return toUserDetailResponse(updated);
    }

    public void changePassword(UUID userId, UserChangePasswordRequest request) {
        User user = findUserById(userId);

        if (!PasswordUtil.verifyPassword(request.currentPassword(), user.getPassword())) {
            throw new PasswordIsNotCorrectException("رمز عبور فعلی شما نادرست وارد شده است");
        }

        user.setPassword(PasswordUtil.hashPassword(request.newPassword()));
        userRepository.save(user);
    }

    public void deleteProfile(UUID userId) {
        User user = findUserById(userId);
        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    public User findUserById(UUID userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("کاربری با این مشخصات یافت نشد"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public UserDetailResponse toUserDetailResponse(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getUserType(),
                user.getUserStatus()
        );
    }

    public UserSummaryResponse toUserSummaryResponse(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getUserType()
        );
    }
}