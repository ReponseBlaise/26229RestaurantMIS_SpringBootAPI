package com.restaurant.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuthResponse {
    private String message;
    private UserResponse user;
    private LocalDateTime authenticatedAt;
}
