package Service;

import dto.user.UserDetailResponse;
import dto.user.UserSummaryResponse;
import dto.user.UserUpdateRequest;
import dto.user.UserChangePasswordRequest;
import entity.User;
import entity.enums.UserStatus;
import Repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                throw new RuntimeException("Email already taken");
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
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(PasswordUtil.hashPassword(request.newPassword()));
        userRepository.save(user);
    }

    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toUserSummaryResponse)
                .collect(Collectors.toList());
    }

    public List<UserSummaryResponse> getUsersByStatus(UserStatus status) {
        return userRepository.findByUserStatus(status).stream()
                .map(this::toUserSummaryResponse)
                .collect(Collectors.toList());
    }

    public void banUser(UUID userId) {
        User user = findUserById(userId);
        user.setUserStatus(UserStatus.BANNED);
        userRepository.save(user);
    }

    public void unbanUser(UUID userId) {
        User user = findUserById(userId);
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    public User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    private UserDetailResponse toUserDetailResponse(User user) {
        return new UserDetailResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getUserType(),
                user.getUserStatus()
        );
    }

    private UserSummaryResponse toUserSummaryResponse(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getUserType()
        );
    }
}