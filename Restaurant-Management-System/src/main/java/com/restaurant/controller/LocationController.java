package com.restaurant.controller;

import com.restaurant.dto.request.LocationRequest;
import com.restaurant.dto.response.ApiResponse;
import com.restaurant.dto.response.LocationResponse;
import com.restaurant.model.Location;
import com.restaurant.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<ApiResponse<LocationResponse>> createLocation(@Valid @RequestBody LocationRequest request) {
        LocationResponse location = locationService.createLocation(request);
        return new ResponseEntity<>(ApiResponse.success("Location created successfully", location), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocationById(@PathVariable Long id) {
        LocationResponse location = locationService.getLocationById(id);
        return ResponseEntity.ok(ApiResponse.success(location));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<LocationResponse>> getLocationByCode(@PathVariable String code) {
        LocationResponse location = locationService.getLocationByCode(code);
        return ResponseEntity.ok(ApiResponse.success(location));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getAllLocations() {
        List<LocationResponse> locations = locationService.getAllLocations();
        return ResponseEntity.ok(ApiResponse.success(locations));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getLocationsByType(@PathVariable Location.LocationType type) {
        List<LocationResponse> locations = locationService.getLocationsByType(type);
        return ResponseEntity.ok(ApiResponse.success(locations));
    }

    @GetMapping("/children/{parentId}")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getChildLocations(@PathVariable Long parentId) {
        List<LocationResponse> children = locationService.getChildLocations(parentId);
        return ResponseEntity.ok(ApiResponse.success(children));
    }
}
