package com.restaurant.controller;

import com.restaurant.dto.request.MealDealRequest;
import com.restaurant.dto.request.MenuItemRequest;
import com.restaurant.model.MealDeal;
import com.restaurant.model.MenuItem;
import com.restaurant.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<MenuItem> createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        MenuItem menuItem = menuService.createMenuItem(request);
        return new ResponseEntity<>(menuItem, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        MenuItem menuItem = menuService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem);
    }

    @GetMapping
    public ResponseEntity<Page<MenuItem>> getAllMenuItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<MenuItem> menuItems = menuService.getAllMenuItems(pageable);
        return ResponseEntity.ok(menuItems);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Page<MenuItem>> getMenuItemsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<MenuItem> menuItems = menuService.getMenuItemsByCategory(category, pageable);
        return ResponseEntity.ok(menuItems);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItem> updateMenuItem(
            @PathVariable Long id,
            @Valid @RequestBody MenuItemRequest request) {
        MenuItem menuItem = menuService.updateMenuItem(id, request);
        return ResponseEntity.ok(menuItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }

    // Meal Deal endpoints (Many-to-Many relationship)
    @PostMapping("/meal-deals")
    public ResponseEntity<MealDeal> createMealDeal(@Valid @RequestBody MealDealRequest request) {
        MealDeal mealDeal = menuService.createMealDeal(request);
        return new ResponseEntity<>(mealDeal, HttpStatus.CREATED);
    }

    @GetMapping("/meal-deals/{id}")
    public ResponseEntity<MealDeal> getMealDealById(@PathVariable Long id) {
        MealDeal mealDeal = menuService.getMealDealById(id);
        return ResponseEntity.ok(mealDeal);
    }

    @GetMapping("/meal-deals")
    public ResponseEntity<Page<MealDeal>> getAllMealDeals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<MealDeal> mealDeals = menuService.getAllMealDeals(pageable);
        return ResponseEntity.ok(mealDeals);
    }
}
