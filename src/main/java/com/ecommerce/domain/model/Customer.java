package com.ecommerce.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Address address;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private MembershipTier membershipTier;
    private List<Order> orderHistory;
    private boolean enabled;
}

