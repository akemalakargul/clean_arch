package com.ecommerce.infrastructure.persistence.mapper;

import com.ecommerce.domain.model.Category;
import com.ecommerce.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CategoryMapper {
    public Set<Category> toDomainSet(Set<CategoryEntity> entities) {
        if (entities == null) return null;
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toSet());
    }

    public Set<CategoryEntity> toEntitySet(Set<Category> domains) {
        if (domains == null) return null;
        return domains.stream()
                .map(this::toEntity)
                .collect(Collectors.toSet());
    }

    public Category toDomain(CategoryEntity entity) {
        if (entity == null) return null;
        
        return Category.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .parentCategory(entity.getParentCategory() != null ? 
                    toDomain(entity.getParentCategory()) : null)
                .build();
    }

    public CategoryEntity toEntity(Category domain) {
        if (domain == null) return null;
        
        return CategoryEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .parentCategory(domain.getParentCategory() != null ? 
                    toEntity(domain.getParentCategory()) : null)
                .build();
    }
} 
