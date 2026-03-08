package com.restaurant.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ReceiptResponse {
    private Long id;
    private String receiptNumber;
    private String orderNumber;
    private Long orderId;
    private BigDecimal totalAmount;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private String generatedByName;
    private LocalDateTime generatedAt;
    private String downloadUrl;
}
