package com.restaurant.repository;

import com.restaurant.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // existsBy method to check if phone already exists
    boolean existsByPhone(String phone);
    
    boolean existsByEmail(String email);
    
    // Province-based query: Find customers by province through their addresses
    @Query("SELECT DISTINCT c FROM Customer c JOIN c.addresses a WHERE a.province = :province")
    List<Customer> findByProvince(@Param("province") String province);
    
    @Query("SELECT DISTINCT c FROM Customer c JOIN c.addresses a WHERE a.province = :province")
    Page<Customer> findByProvince(@Param("province") String province, Pageable pageable);
    
    // Find customers by city
    @Query("SELECT DISTINCT c FROM Customer c JOIN c.addresses a WHERE a.city = :city")
    Page<Customer> findByCity(@Param("city") String city, Pageable pageable);
}
