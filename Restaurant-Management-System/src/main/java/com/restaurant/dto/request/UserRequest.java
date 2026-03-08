package com.restaurant.dto.request;

import com.restaurant.model.User;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @NotNull(message = "Role is required")
    private User.UserRole role;

    @Pattern(regexp = "^\\+?250[0-9]{9}$", message = "Invalid Rwandan phone number")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;
}
