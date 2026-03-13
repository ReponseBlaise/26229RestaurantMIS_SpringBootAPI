package com.restaurant.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CustomerRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?250[0-9]{9}$", message = "Invalid Rwandan phone number")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private Long villageId; // Optional - links to Cell → Sector → District → Province

    private String streetAddress;
}
