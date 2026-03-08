package com.restaurant.repository;

import com.restaurant.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    Optional<Receipt> findByOrderId(Long orderId);
    
    boolean existsByOrderId(Long orderId);
    
    boolean existsByReceiptNumber(String receiptNumber);
}
