package com.restaurant.service;

import com.restaurant.dto.request.OrderRequest;
import com.restaurant.dto.response.OrderResponse;
import com.restaurant.exception.BadRequestException;
import com.restaurant.exception.ResourceNotFoundException;
import com.restaurant.model.*;
import com.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest request, Long waiterId) {
        Customer customer = customerRepository.findById(request.getCustomerId())
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        User waiter = userRepository.findById(waiterId)
            .orElseThrow(() -> new ResourceNotFoundException("Waiter not found"));

        if (waiter.getRole() != User.UserRole.WAITER) {
            throw new BadRequestException("Only waiters can create orders");
        }

        String orderNumber = generateOrderNumber();
        
        Order order = Order.builder()
            .orderNumber(orderNumber)
            .customer(customer)
            .waiter(waiter)
            .tableNumber(request.getTableNumber())
            .orderDate(LocalDateTime.now())
            .status(Order.OrderStatus.PENDING)
            .paymentStatus(Order.PaymentStatus.PENDING)
            .totalAmount(BigDecimal.ZERO)
            .build();

        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

            if (!menuItem.getIsAvailable()) {
                throw new BadRequestException("Menu item " + menuItem.getName() + " is not available");
            }

            BigDecimal subtotal = menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .quantity(itemRequest.getQuantity())
                .unitPrice(menuItem.getPrice())
                .subtotal(subtotal)
                .specialInstructions(itemRequest.getSpecialInstructions())
                .build();

            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);
        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        return mapToResponse(order, orderItems);
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        return mapToResponse(order, orderItems);
    }

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            return mapToResponse(order, orderItems);
        });
    }

    public Page<OrderResponse> getOrdersByWaiterId(Long waiterId, Pageable pageable) {
        return orderRepository.findByWaiterId(waiterId, pageable).map(order -> {
            List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());
            return mapToResponse(order, orderItems);
        });
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        order.setStatus(status);
        order = orderRepository.save(order);
        
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(id);
        return mapToResponse(order, orderItems);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis();
    }

    private OrderResponse mapToResponse(Order order, List<OrderItem> orderItems) {
        List<OrderResponse.OrderItemResponse> itemResponses = orderItems.stream()
            .map(item -> OrderResponse.OrderItemResponse.builder()
                .id(item.getId())
                .menuItemId(item.getMenuItem().getId())
                .menuItemName(item.getMenuItem().getName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .specialInstructions(item.getSpecialInstructions())
                .build())
            .toList();

        return OrderResponse.builder()
            .id(order.getId())
            .orderNumber(order.getOrderNumber())
            .customerId(order.getCustomer().getId())
            .customerName(order.getCustomer().getName())
            .waiterId(order.getWaiter().getId())
            .waiterName(order.getWaiter().getFullName())
            .tableNumber(order.getTableNumber())
            .orderDate(order.getOrderDate())
            .totalAmount(order.getTotalAmount())
            .status(order.getStatus())
            .paymentMethod(order.getPaymentMethod())
            .paymentStatus(order.getPaymentStatus())
            .items(itemResponses)
            .createdAt(order.getCreatedAt())
            .build();
    }
}
