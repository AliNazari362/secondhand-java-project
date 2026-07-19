package com.secondhand.service;

import com.secondhand.dto.user.UserLoginRequest;
import com.secondhand.dto.user.UserRegisterRequest;
import com.secondhand.dto.user.UserDetailResponse;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.UserStatus;
import com.secondhand.entity.enums.UserType;
import com.secondhand.exception.*;
import com.secondhand.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user authentication: registration and login.
 * <p>
 * Handles new user creation with password hashing, and authenticates existing users
 * by verifying credentials and account status before issuing a JWT token.
 * </p>
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Constructs an {@code AuthService} with the required repository and service dependencies.
     *
     * @param userRepository the repository used to persist and look up user records
     * @param userService    the service used for user lookups and response mapping
     */
    public AuthService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Registers a new user account.
     * <p>
     * Validates that the provided email is not already in use, hashes the password,
     * creates the user with a {@link UserStatus#ACTIVE} status and {@link UserType#USER} role,
     * and persists the record.
     * </p>
     *
     * @param request the registration data including email, password, full name, and phone number
     * @return a {@link UserDetailResponse} representing the newly created user
     * @throws ResourceAlreadyExistsException if a user with the given email already exists
     */
    public UserDetailResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("این ایمیل پیش از این در سیستم ثبت شده است");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(PasswordUtil.hashPassword(request.password()));
        user.setFullName(request.fullName());
        user.setPhoneNumber(request.phoneNumber());
        user.setUserType(UserType.USER);
        user.setUserStatus(UserStatus.ACTIVE);

        User saved = userRepository.save(user);
        return userService.toUserDetailResponse(saved);
    }

    /**
     * Authenticates a user and returns a signed JWT token on success.
     * <p>
     * Performs the following checks in order:
     * <ol>
     *   <li>Verifies the email exists in the system.</li>
     *   <li>Verifies the provided password matches the stored hash.</li>
     *   <li>Ensures the account is not {@link UserStatus#BANNED}.</li>
     *   <li>Ensures the account is not {@link UserStatus#DELETED}.</li>
     * </ol>
     * </p>
     *
     * @param request the login credentials containing email and password
     * @return a signed JWT token string valid for 24 hours
     * @throws BadRequestException  if the email is not found or the password is incorrect
     * @throws ForbiddenException   if the account is banned or has been deleted
     */
    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadRequestException("ایمیل نامعتبر است"));

        if (!PasswordUtil.verifyPassword(request.password(), user.getPassword())) {
            throw new BadRequestException("رمز عبور اشتباه وارد شده است");
        }

        if (user.getUserStatus() == UserStatus.BANNED) {
            throw new ForbiddenException("حساب شما تعلیق شده است؛ اجازه ورود ندارید");
        }

        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new ForbiddenException("این حساب کاربری حذف شده است و امکان ورود وجود ندارد");
        }

        return JwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getUserType().name()
        );
    }
}
