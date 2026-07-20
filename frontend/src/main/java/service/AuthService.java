package service;

import model.request.LoginRequest;
import model.response.LoginResponse;

public class AuthService {

    public LoginResponse login(LoginRequest request) throws Exception {
        String json = ApiClient.post("/auth/login", request);
        return ApiClient.fromJson(json, LoginResponse.class);
    }
}