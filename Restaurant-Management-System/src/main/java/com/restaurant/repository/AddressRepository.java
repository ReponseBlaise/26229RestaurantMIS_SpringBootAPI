package com.restaurant.repository;

import com.restaurant.model.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomerId(Long customerId);
    
    // Query by Province through the location hierarchy
    // Village -> Cell -> Sector -> District -> Province
    Page<Address> findByVillage_Parent_Parent_Parent_Parent_Name(String provinceName, Pageable pageable);
    
    // Query by District
    Page<Address> findByVillage_Parent_Parent_Parent_Name(String districtName, Pageable pageable);
    
    // Query by Sector
    Page<Address> findByVillage_Parent_Parent_Name(String sectorName, Pageable pageable);
    
    // Query by Cell
    Page<Address> findByVillage_Parent_Name(String cellName, Pageable pageable);
    
    // Query by Village
    Page<Address> findByVillage_Name(String villageName, Pageable pageable);
    
    boolean existsByCustomerIdAndIsDefaultTrue(Long customerId);
}
