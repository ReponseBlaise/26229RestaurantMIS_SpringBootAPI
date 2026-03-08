package com.restaurant.service;

import com.restaurant.dto.response.ReceiptResponse;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.Order;
import com.restaurant.model.Receipt;
import com.restaurant.model.User;
import com.restaurant.repository.OrderRepository;
import com.restaurant.repository.ReceiptRepository;
import com.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Value("${receipt.storage.path:./receipts}")
    private String receiptStoragePath;

    @Transactional
    public ReceiptResponse generateReceipt(Long orderId, Long cashierId) {
        if (receiptRepository.existsByOrderId(orderId)) {
            throw new BadRequestException("Receipt already exists for this order");
        }

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User cashier = userRepository.findById(cashierId)
            .orElseThrow(() -> new ResourceNotFoundException("Cashier not found"));

        if (cashier.getRole() != User.UserRole.CASHIER) {
            throw new BadRequestException("Only cashiers can generate receipts");
        }

        if (order.getPaymentStatus() != Order.PaymentStatus.PAID) {
            throw new BadRequestException("Order must be paid before generating receipt");
        }

        String receiptNumber = generateReceiptNumber();
        BigDecimal taxAmount = order.getTotalAmount().multiply(BigDecimal.valueOf(0.18)); // 18% VAT
        BigDecimal discountAmount = BigDecimal.ZERO;

        Receipt receipt = Receipt.builder()
            .order(order)
            .receiptNumber(receiptNumber)
            .generatedBy(cashier)
            .totalAmount(order.getTotalAmount())
            .taxAmount(taxAmount)
            .discountAmount(discountAmount)
            .generatedAt(LocalDateTime.now())
            .build();

        try {
            String pdfPath = generateReceiptPDF(receipt, order);
            receipt.setPdfPath(pdfPath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate receipt PDF", e);
        }

        receipt = receiptRepository.save(receipt);
        return mapToResponse(receipt);
    }

    public ReceiptResponse getReceiptByOrderId(Long orderId) {
        Receipt receipt = receiptRepository.findByOrderId(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Receipt not found for this order"));
        return mapToResponse(receipt);
    }

    public byte[] downloadReceipt(Long receiptId) throws IOException {
        Receipt receipt = receiptRepository.findById(receiptId)
            .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));
        
        Path path = Paths.get(receipt.getPdfPath());
        return Files.readAllBytes(path);
    }

    private String generateReceiptNumber() {
        return "RCP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + 
               "-" + System.currentTimeMillis() % 10000;
    }

    private String generateReceiptPDF(Receipt receipt, Order order) throws IOException {
        Path directory = Paths.get(receiptStoragePath);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String fileName = receipt.getReceiptNumber() + ".txt";
        Path filePath = directory.resolve(fileName);

        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write("========================================\n");
            writer.write("       RESTAURANT RECEIPT\n");
            writer.write("========================================\n\n");
            writer.write("Receipt Number: " + receipt.getReceiptNumber() + "\n");
            writer.write("Order Number: " + order.getOrderNumber() + "\n");
            writer.write("Date: " + receipt.getGeneratedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\n");
            writer.write("Customer: " + order.getCustomer().getName() + "\n");
            writer.write("Table: " + order.getTableNumber() + "\n");
            writer.write("Waiter: " + order.getWaiter().getFullName() + "\n");
            writer.write("Cashier: " + receipt.getGeneratedBy().getFullName() + "\n\n");
            writer.write("----------------------------------------\n");
            writer.write("ITEMS:\n");
            writer.write("----------------------------------------\n");
            
            order.getOrderItems().forEach(item -> {
                try {
                    writer.write(String.format("%s x%d - %,.0f RWF\n", 
                        item.getMenuItem().getName(), 
                        item.getQuantity(), 
                        item.getSubtotal()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            
            writer.write("----------------------------------------\n");
            writer.write(String.format("Subtotal: %,.0f RWF\n", receipt.getTotalAmount()));
            writer.write(String.format("VAT (18%%): %,.0f RWF\n", receipt.getTaxAmount()));
            writer.write(String.format("Discount: %,.0f RWF\n", receipt.getDiscountAmount()));
            writer.write(String.format("TOTAL: %,.0f RWF\n", 
                receipt.getTotalAmount().add(receipt.getTaxAmount()).subtract(receipt.getDiscountAmount())));
            writer.write("========================================\n");
            writer.write("     Thank you for your visit!\n");
            writer.write("========================================\n");
        }

        return filePath.toString();
    }

    private ReceiptResponse mapToResponse(Receipt receipt) {
        return ReceiptResponse.builder()
            .id(receipt.getId())
            .receiptNumber(receipt.getReceiptNumber())
            .orderNumber(receipt.getOrder().getOrderNumber())
            .orderId(receipt.getOrder().getId())
            .totalAmount(receipt.getTotalAmount())
            .taxAmount(receipt.getTaxAmount())
            .discountAmount(receipt.getDiscountAmount())
            .generatedByName(receipt.getGeneratedBy().getFullName())
            .generatedAt(receipt.getGeneratedAt())
            .downloadUrl("/api/receipts/" + receipt.getId() + "/download")
            .build();
    }
}
