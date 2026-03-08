package com.restaurant.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class MenuItemRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 100)
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotBlank(message = "Category is required")
    private String category;

    private Boolean isAvailable = true;

    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    private Integer preparationTime = 15;
}
