package com.restaurant.controller;

import com.restaurant.dto.request.LocationRequest;
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
    public ResponseEntity<LocationResponse> createLocation(@Valid @RequestBody LocationRequest request) {
        LocationResponse response = locationService.createLocation(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(@PathVariable Long id) {
        LocationResponse response = locationService.getLocationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<LocationResponse> getLocationByCode(@PathVariable String code) {
        LocationResponse response = locationService.getLocationByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAllLocations() {
        List<LocationResponse> locations = locationService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<LocationResponse>> getLocationsByType(@PathVariable Location.LocationType type) {
        List<LocationResponse> locations = locationService.getLocationsByType(type);
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/children/{parentId}")
    public ResponseEntity<List<LocationResponse>> getChildLocations(@PathVariable Long parentId) {
        List<LocationResponse> children = locationService.getChildLocations(parentId);
        return ResponseEntity.ok(children);
    }
}
