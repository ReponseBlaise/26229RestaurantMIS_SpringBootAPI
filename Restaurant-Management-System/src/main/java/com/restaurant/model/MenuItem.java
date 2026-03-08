package com.restaurant.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // Isombe, Brochettes, Ugali, Sambaza, etc.

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price; // In RWF (Rwandan Francs)

    @Column(nullable = false, length = 50)
    private String category; // Main Course, Appetizer, Beverage, Dessert

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "preparation_time")
    private Integer preparationTime = 15; // in minutes

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // One-to-Many: MenuItem can be in multiple order items
    @OneToMany(mappedBy = "menuItem")
    private List<OrderItem> orderItems;

    // Many-to-Many: MenuItem can be part of multiple meal deals
    @ManyToMany(mappedBy = "menuItems")
    private Set<MealDeal> mealDeals = new HashSet<>();
}
