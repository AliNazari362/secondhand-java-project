package com.secondhand.service;

import com.secondhand.dto.user.UserLoginRequest;
import com.secondhand.dto.user.UserRegisterRequest;
import com.secondhand.dto.user.UserDetailResponse;
import com.secondhand.entity.User;
import com.secondhand.entity.enums.UserStatus;
import com.secondhand.entity.enums.UserType;
import com.secondhand.repository.UserRepository;
import com.secondhand.exception.AccountIsNotAccessibleException;
import com.secondhand.exception.EmailAlreadyExistException;
import com.secondhand.exception.IllegalEmailException;
import com.secondhand.exception.PasswordIsNotCorrectException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;

    public AuthService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public UserDetailResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistException("این ایمیل پیش از این در سیستم ثبت شده است");
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

    public String login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalEmailException("ایمیل نامعتبر است"));

        if (!PasswordUtil.verifyPassword(request.password(), user.getPassword())) {
            throw new PasswordIsNotCorrectException("رمز عبور اشتباه وارد شده است");
        }

        if (user.getUserStatus() == UserStatus.BANNED) {
            throw new AccountIsNotAccessibleException("حساب شما تعلیق شده است؛ اجازه ورود ندارید");
        }

        return JwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getUserType().name()
        );
    }
}