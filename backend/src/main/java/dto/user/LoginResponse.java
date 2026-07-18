package dto.user;

import entity.enums.UserType;

import java.util.UUID;

public record LoginResponse(
        String token,
        UUID userId,
        String fullName,
        UserType role
) {}