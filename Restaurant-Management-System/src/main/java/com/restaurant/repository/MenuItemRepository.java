package com.restaurant.repository;

import com.restaurant.model.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    Page<MenuItem> findByCategory(String category, Pageable pageable);
    
    Page<MenuItem> findByIsAvailable(Boolean isAvailable, Pageable pageable);
    
    Page<MenuItem> findByCategoryAndIsAvailable(String category, Boolean isAvailable, Pageable pageable);
    
    boolean existsByName(String name);
}
