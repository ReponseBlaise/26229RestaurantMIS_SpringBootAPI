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
    boolean existsByPhone(String phone);
    
    boolean existsByEmail(String email);
    
    // Province-based query: Find customers by province through location hierarchy
    @Query("SELECT DISTINCT c FROM Customer c WHERE c.village IS NOT NULL AND c.village.parent.parent.parent.parent.name = :provinceName")
    List<Customer> findByProvince(@Param("provinceName") String provinceName);
    
    @Query("SELECT DISTINCT c FROM Customer c WHERE c.village IS NOT NULL AND c.village.parent.parent.parent.parent.name = :provinceName")
    Page<Customer> findByProvince(@Param("provinceName") String provinceName, Pageable pageable);
    
    // Find customers by district
    @Query("SELECT DISTINCT c FROM Customer c WHERE c.village IS NOT NULL AND c.village.parent.parent.parent.name = :districtName")
    Page<Customer> findByDistrict(@Param("districtName") String districtName, Pageable pageable);
    
    // Find customers by sector
    @Query("SELECT DISTINCT c FROM Customer c WHERE c.village IS NOT NULL AND c.village.parent.parent.name = :sectorName")
    Page<Customer> findBySector(@Param("sectorName") String sectorName, Pageable pageable);
    
    // Find customers by cell
    @Query("SELECT DISTINCT c FROM Customer c WHERE c.village IS NOT NULL AND c.village.parent.name = :cellName")
    Page<Customer> findByCell(@Param("cellName") String cellName, Pageable pageable);
    
    // Find customers by village
    @Query("SELECT DISTINCT c FROM Customer c WHERE c.village IS NOT NULL AND c.village.name = :villageName")
    Page<Customer> findByVillage(@Param("villageName") String villageName, Pageable pageable);
}
