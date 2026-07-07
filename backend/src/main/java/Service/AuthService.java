package Service;

import dto.user.UserRegisterRequest;
import dto.user.UserDetailResponse;
import entity.User;
import entity.enums.UserStatus;
import entity.enums.UserType;
import Repository.UserRepository;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetailResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPassword(PasswordUtil.hashPassword(request.password()));
        user.setFullName(request.fullName());
        user.setPhoneNumber(request.phoneNumber());
        user.setUserType(UserType.USER);
        user.setUserStatus(UserStatus.ACTIVE);

        User saved = userRepository.save(user);
        return toUserDetailResponse(saved);
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getUserStatus() == UserStatus.BANNED) {
            throw new RuntimeException("Account is banned");
        }

        return JwtUtil.generateToken(
                user.getId().toString(),
                user.getEmail(),
                user.getUserType().name()
        );
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
}