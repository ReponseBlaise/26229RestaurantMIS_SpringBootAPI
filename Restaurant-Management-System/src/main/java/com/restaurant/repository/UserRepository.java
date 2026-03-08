package com.restaurant.repository;

import com.restaurant.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    
    // existsBy method to check if username already exists
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    Page<User> findByRole(User.UserRole role, Pageable pageable);
    
    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
}
