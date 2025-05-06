package com.ecommerce.infrastructure.persistence.mapper;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductStatus;
import com.ecommerce.infrastructure.persistence.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final CategoryMapper categoryMapper;

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        
        return Product.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .basePrice(entity.getBasePrice())
                .currentPrice(entity.getCurrentPrice())
                .categories(categoryMapper.toDomainSet(entity.getCategories()))
                .imageUrl(entity.getImageUrl())
                .stockQuantity(entity.getStockQuantity())
                .status(entity.getStatus())
                .weight(entity.getWeight())
                .dimensions(entity.getDimensions())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ProductEntity toEntity(Product domain) {
        if (domain == null) return null;
        
        return ProductEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .basePrice(domain.getBasePrice())
                .currentPrice(domain.getCurrentPrice())
                .categories(categoryMapper.toEntitySet(domain.getCategories()))
                .imageUrl(domain.getImageUrl())
                .stockQuantity(domain.getStockQuantity())
                .status(domain.getStatus())
                .weight(domain.getWeight())
                .dimensions(domain.getDimensions())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
} 
