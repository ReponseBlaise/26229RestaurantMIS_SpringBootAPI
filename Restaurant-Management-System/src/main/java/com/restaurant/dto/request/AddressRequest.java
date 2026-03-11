package com.restaurant.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddressRequest {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Village ID is required")
    private Long villageId; // Only need village - it links to Cell → Sector → District → Province

    private String streetAddress;

    private String postalCode;

    private Boolean isDefault = false;
}
