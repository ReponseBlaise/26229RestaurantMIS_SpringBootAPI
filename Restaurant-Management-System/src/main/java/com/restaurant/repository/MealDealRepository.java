package com.restaurant.repository;

import com.restaurant.model.MealDeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealDealRepository extends JpaRepository<MealDeal, Long> {
    Page<MealDeal> findByIsActive(Boolean isActive, Pageable pageable);
    
    boolean existsByName(String name);
}
