package com.restaurant.service;

import com.restaurant.dto.request.MealDealRequest;
import com.restaurant.dto.request.MenuItemRequest;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.MealDeal;
import com.restaurant.model.MenuItem;
import com.restaurant.repository.MealDealRepository;
import com.restaurant.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MenuService {
    private final MenuItemRepository menuItemRepository;
    private final MealDealRepository mealDealRepository;

    @Transactional
    public MenuItem createMenuItem(MenuItemRequest request) {
        if (menuItemRepository.existsByName(request.getName())) {
            throw new BadRequestException("Menu item with this name already exists");
        }

        MenuItem menuItem = MenuItem.builder()
            .name(request.getName())
            .description(request.getDescription())
            .price(request.getPrice())
            .category(request.getCategory())
            .isAvailable(request.getIsAvailable())
            .preparationTime(request.getPreparationTime())
            .build();

        return menuItemRepository.save(menuItem);
    }

    public MenuItem getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
    }

    public Page<MenuItem> getAllMenuItems(Pageable pageable) {
        return menuItemRepository.findAll(pageable);
    }

    public Page<MenuItem> getMenuItemsByCategory(String category, Pageable pageable) {
        return menuItemRepository.findByCategory(category, pageable);
    }

    @Transactional
    public MenuItem updateMenuItem(Long id, MenuItemRequest request) {
        MenuItem menuItem = getMenuItemById(id);

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setCategory(request.getCategory());
        menuItem.setIsAvailable(request.getIsAvailable());
        menuItem.setPreparationTime(request.getPreparationTime());

        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu item not found");
        }
        menuItemRepository.deleteById(id);
    }

    // Meal Deal operations (Many-to-Many relationship)
    @Transactional
    public MealDeal createMealDeal(MealDealRequest request) {
        if (mealDealRepository.existsByName(request.getName())) {
            throw new BadRequestException("Meal deal with this name already exists");
        }

        Set<MenuItem> menuItems = new HashSet<>();
        for (Long menuItemId : request.getMenuItemIds()) {
            MenuItem menuItem = getMenuItemById(menuItemId);
            menuItems.add(menuItem);
        }

        MealDeal mealDeal = MealDeal.builder()
            .name(request.getName())
            .description(request.getDescription())
            .dealPrice(request.getDealPrice())
            .isActive(request.getIsActive())
            .menuItems(menuItems)
            .build();

        return mealDealRepository.save(mealDeal);
    }

    public MealDeal getMealDealById(Long id) {
        return mealDealRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Meal deal not found"));
    }

    public Page<MealDeal> getAllMealDeals(Pageable pageable) {
        return mealDealRepository.findAll(pageable);
    }
}
