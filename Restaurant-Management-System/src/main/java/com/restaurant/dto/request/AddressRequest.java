package com.restaurant.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddressRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Province is required")
    private String province; // Kigali, Eastern, Western, Northern, Southern

    @NotBlank(message = "City is required")
    private String city;

    private String district;

    @NotBlank(message = "Street address is required")
    private String streetAddress;

    private String postalCode;

    private Boolean isDefault = false;
}
