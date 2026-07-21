package service;

import model.request.LoginRequest;
import model.request.UserRegisterRequest;
import model.response.LoginResponse;
import model.response.UserDto;
import utils.SessionManager;

public class AuthService {

    private static final ApiClient api = ApiClient.getInstance();

    public static LoginResponse login(LoginRequest request) throws Exception {
        String response = api.post("/auth/login", request);
        return api.fromJson(response, LoginResponse.class);
    }

    public static UserDto register(UserRegisterRequest request) throws Exception {
        String response = api.post("/auth/register", request);
        return api.fromJson(response, UserDto.class);
    }

    public static void logout() {
        SessionManager.clear();
    }
}
