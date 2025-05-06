package com.ecommerce.domain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private BigDecimal currentPrice;
    private Set<Category> categories;
    private String imageUrl;
    private Integer stockQuantity;
    private ProductStatus status;
    private BigDecimal weight;
    private BigDecimal dimensions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
