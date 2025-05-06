package com.ecommerce.infrastructure.persistence;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.port.ProductRepository;
import com.ecommerce.infrastructure.persistence.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaProductPortAdapter implements ProductRepository {
    private final JpaProductRepository jpaProductRepository;
    private final ProductMapper productMapper;

    @Override
    public Product save(Product product) {
        var entity = productMapper.toEntity(product);
        entity = jpaProductRepository.save(entity);
        return productMapper.toDomain(entity);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jpaProductRepository.findById(id)
                .map(productMapper::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll().stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpaProductRepository.deleteById(id);
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return jpaProductRepository.findByCategoryId(categoryId).stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        return jpaProductRepository.findByNameContainingIgnoreCase(name).stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByDescriptionContaining(String description) {
        return jpaProductRepository.findByDescriptionContainingIgnoreCase(description).stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNameOrDescriptionContaining(String keyword) {
        return jpaProductRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword).stream()
                .map(productMapper::toDomain)
                .collect(Collectors.toList());
    }
} 
