package com.restaurant.dto.request;

import com.restaurant.model.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationRequest {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Code is required")
    private String code;
    
    @NotNull(message = "Type is required")
    private Location.LocationType type;
    
    private Long parentId; // null for Province, required for others
}
