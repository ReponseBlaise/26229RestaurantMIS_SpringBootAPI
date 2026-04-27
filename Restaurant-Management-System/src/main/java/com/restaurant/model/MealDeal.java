package com.restaurant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "meal_deals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MealDeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // Family Combo, Lunch Special, etc.

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "deal_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal dealPrice; // Discounted price in RWF

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @JsonIgnore
    @ManyToMany
    @JoinTable(
        name = "meal_deal_items",
        joinColumns = @JoinColumn(name = "meal_deal_id"),
        inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    private Set<MenuItem> menuItems = new HashSet<>();
}
