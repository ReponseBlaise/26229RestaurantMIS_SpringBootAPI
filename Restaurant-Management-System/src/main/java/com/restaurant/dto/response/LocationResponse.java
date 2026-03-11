package com.restaurant.dto.response;

import com.restaurant.model.Location;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationResponse {
    private Long id;
    private String name;
    private String code;
    private Location.LocationType type;
    private Long parentId;
    private String parentName;
    
    // Full hierarchy for easy display
    private String province;
    private String district;
    private String sector;
    private String cell;
    private String village;
}
