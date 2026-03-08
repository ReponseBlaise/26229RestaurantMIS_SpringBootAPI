package com.restaurant.dto.response;

import com.restaurant.model.User;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String username;
    private String fullName;
    private User.UserRole role;
    private String phone;
    private String email;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
