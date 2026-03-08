package com.restaurant.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class MealDealRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Deal price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal dealPrice;

    @NotEmpty(message = "Meal deal must contain at least one menu item")
    private Set<Long> menuItemIds;

    private Boolean isActive = true;
}
