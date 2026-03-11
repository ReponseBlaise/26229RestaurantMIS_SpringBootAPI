package com.restaurant.repository;

import com.restaurant.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    Optional<Location> findByCode(String code);
    
    List<Location> findByType(Location.LocationType type);
    
    List<Location> findByParent(Location parent);
    
    @Query("SELECT l FROM Location l WHERE l.type = :type AND l.parent IS NULL")
    List<Location> findProvinces();
    
    @Query("SELECT l FROM Location l WHERE l.type = 'VILLAGE' AND " +
           "l.parent.parent.parent.parent.name = :provinceName")
    List<Location> findVillagesByProvince(@Param("provinceName") String provinceName);
}
