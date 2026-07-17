package Service;

import DTO.user.UserLoginRequest;
import DTO.user.UserRegisterRequest;
import DTO.user.UserDetailResponse;
import Entity.User;
import Entity.enums.UserStatus;
import Entity.enums.UserType;
import Repository.UserRepository;
import SpecialException.AccountIsNotAccessibleException;
import SpecialException.EmailAlreadyExistException;
import SpecialException.IllegalEmailException;
import SpecialException.PasswordIsNotCorrectException;

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