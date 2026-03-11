package com.restaurant.service;

import com.restaurant.dto.request.LocationRequest;
import com.restaurant.dto.response.LocationResponse;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.Location;
import com.restaurant.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    @Transactional
    public LocationResponse createLocation(LocationRequest request) {
        // Validate parent requirement
        if (request.getType() != Location.LocationType.PROVINCE && request.getParentId() == null) {
            throw new BadRequestException("Parent location is required for " + request.getType());
        }

        if (request.getType() == Location.LocationType.PROVINCE && request.getParentId() != null) {
            throw new BadRequestException("Province cannot have a parent");
        }

        // Check if code already exists
        if (locationRepository.findByCode(request.getCode()).isPresent()) {
            throw new BadRequestException("Location code already exists");
        }

        Location parent = null;
        if (request.getParentId() != null) {
            parent = locationRepository.findById(request.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent location not found"));
            
            // Validate hierarchy
            validateHierarchy(request.getType(), parent.getType());
        }

        Location location = Location.builder()
            .name(request.getName())
            .code(request.getCode())
            .type(request.getType())
            .parent(parent)
            .build();

        location = locationRepository.save(location);
        return mapToResponse(location);
    }

    public LocationResponse getLocationById(Long id) {
        Location location = locationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Location not found"));
        return mapToResponse(location);
    }

    public LocationResponse getLocationByCode(String code) {
        Location location = locationRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Location not found with code: " + code));
        return mapToResponse(location);
    }

    public List<LocationResponse> getAllLocations() {
        return locationRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<LocationResponse> getLocationsByType(Location.LocationType type) {
        return locationRepository.findByType(type).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<LocationResponse> getChildLocations(Long parentId) {
        Location parent = locationRepository.findById(parentId)
            .orElseThrow(() -> new ResourceNotFoundException("Parent location not found"));
        
        return locationRepository.findByParent(parent).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private void validateHierarchy(Location.LocationType childType, Location.LocationType parentType) {
        boolean valid = switch (childType) {
            case DISTRICT -> parentType == Location.LocationType.PROVINCE;
            case SECTOR -> parentType == Location.LocationType.DISTRICT;
            case CELL -> parentType == Location.LocationType.SECTOR;
            case VILLAGE -> parentType == Location.LocationType.CELL;
            default -> false;
        };

        if (!valid) {
            throw new BadRequestException(
                String.format("Invalid hierarchy: %s cannot be child of %s", childType, parentType)
            );
        }
    }

    private LocationResponse mapToResponse(Location location) {
        LocationResponse response = LocationResponse.builder()
            .id(location.getId())
            .name(location.getName())
            .code(location.getCode())
            .type(location.getType())
            .build();

        if (location.getParent() != null) {
            response.setParentId(location.getParent().getId());
            response.setParentName(location.getParent().getName());
        }

        // Build full hierarchy
        buildFullHierarchy(location, response);

        return response;
    }

    private void buildFullHierarchy(Location location, LocationResponse response) {
        Location current = location;
        
        while (current != null) {
            switch (current.getType()) {
                case PROVINCE -> response.setProvince(current.getName());
                case DISTRICT -> response.setDistrict(current.getName());
                case SECTOR -> response.setSector(current.getName());
                case CELL -> response.setCell(current.getName());
                case VILLAGE -> response.setVillage(current.getName());
            }
            current = current.getParent();
        }
    }
}
