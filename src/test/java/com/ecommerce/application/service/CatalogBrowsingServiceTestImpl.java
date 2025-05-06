package com.ecommerce.application.service;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductStatus;
import com.ecommerce.domain.port.ProductRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogBrowsingServiceTestImpl {
    private final ProductRepository productRepository;
    
    public CatalogBrowsingServiceTestImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllActiveProducts() {
        return productRepository.findAll().stream()
                .filter(product -> product.getStatus() == ProductStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .filter(product -> product.getStatus() == ProductStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameOrDescriptionContaining(keyword).stream()
                .filter(product -> product.getStatus() == ProductStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContaining(name).stream()
                .filter(product -> product.getStatus() == ProductStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<Product> searchProductsByDescription(String description) {
        return productRepository.findByDescriptionContaining(description).stream()
                .filter(product -> product.getStatus() == ProductStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    public List<Product> sortProductsByPriceAsc(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparing(Product::getCurrentPrice))
                .collect(Collectors.toList());
    }

    public List<Product> sortProductsByPriceDesc(List<Product> products) {
        return products.stream()
                .sorted(Comparator.comparing(Product::getCurrentPrice).reversed())
                .collect(Collectors.toList());
    }

    public List<Product> filterByPriceRange(List<Product> products, BigDecimal minPrice, BigDecimal maxPrice) {
        return products.stream()
                .filter(product -> {
                    BigDecimal price = product.getCurrentPrice();
                    return (minPrice == null || price.compareTo(minPrice) >= 0) &&
                           (maxPrice == null || price.compareTo(maxPrice) <= 0);
                })
                .collect(Collectors.toList());
    }
} 
