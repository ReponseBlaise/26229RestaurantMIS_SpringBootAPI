package com.restaurant.controller;

import com.restaurant.dto.response.ApiResponse;
import com.restaurant.dto.response.ReceiptResponse;
import com.restaurant.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {
    private final ReceiptService receiptService;

    @PostMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ReceiptResponse>> generateReceipt(
            @PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long cashierId) {
        ReceiptResponse response = receiptService.generateReceipt(orderId, cashierId);
        return new ResponseEntity<>(ApiResponse.success("Receipt generated successfully", response), HttpStatus.CREATED);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ReceiptResponse>> getReceiptByOrderId(@PathVariable Long orderId) {
        ReceiptResponse response = receiptService.getReceiptByOrderId(orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{receiptId}/download")
    public ResponseEntity<byte[]> downloadReceipt(@PathVariable Long receiptId) throws IOException {
        byte[] receiptData = receiptService.downloadReceipt(receiptId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", "receipt-" + receiptId + ".txt");
        
        return new ResponseEntity<>(receiptData, headers, HttpStatus.OK);
    }
}
