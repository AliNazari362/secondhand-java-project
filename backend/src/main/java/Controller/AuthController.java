package Controller;

import DTO.user.UserDetailResponse;
import DTO.user.UserLoginRequest;
import DTO.user.UserRegisterRequest;
import Service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("register")
    public UserDetailResponse register(@RequestBody UserRegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }

    @PostMapping("login")
    public String login(@RequestBody UserLoginRequest loginRequest) {
        return authService.login(loginRequest);
    }
}
