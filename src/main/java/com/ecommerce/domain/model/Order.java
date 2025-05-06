package com.ecommerce.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    private Long id;
    private Customer customer;
    private LocalDateTime orderDate;
    private OrderStatus status;
    private Address shippingAddress;
    private Address billingAddress;
    private PaymentMethod paymentMethod;
    private List<OrderItem> orderItems;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal taxAmount;
    private BigDecimal discountAmount;
    private BigDecimal totalPrice;
    private String trackingInformation;
}

