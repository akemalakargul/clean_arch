package com.ecommerce.infrastructure.persistence;

import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductStatus;
import com.ecommerce.domain.port.ProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Primary
@Profile("mock")
public class MockProductPortAdapter implements ProductRepository {
    
    private final Map<Long, Product> products = new HashMap<>();
    private final Map<Long, Category> categories = new HashMap<>();
    private Long nextProductId = 1L;
    private Long nextCategoryId = 1L;

    public MockProductPortAdapter() {
        Category electronics = createCategory("Electronics", "Electronic devices and gadgets");
        Category clothing = createCategory("Clothing", "Apparel and fashion items");
        Category books = createCategory("Books", "Books and publications");
        Category homeDecor = createCategory("Home Decor", "Items for home decoration");
        
        createProduct("Smartphone X", "Latest smartphone with advanced features",
                new BigDecimal("699.99"), new BigDecimal("649.99"), 
                Set.of(electronics), 50, ProductStatus.ACTIVE);
                
        createProduct("Laptop Pro", "High-performance laptop for professionals", 
                new BigDecimal("1299.99"), new BigDecimal("1199.99"), 
                Set.of(electronics), 25, ProductStatus.ACTIVE);
                
        createProduct("Wireless Headphones", "Noise-cancelling wireless headphones", 
                new BigDecimal("199.99"), new BigDecimal("179.99"), 
                Set.of(electronics), 100, ProductStatus.ACTIVE);
                
        createProduct("Classic T-Shirt", "Comfortable cotton t-shirt", 
                new BigDecimal("29.99"), new BigDecimal("24.99"), 
                Set.of(clothing), 200, ProductStatus.ACTIVE);
                
        createProduct("Designer Jeans", "Premium denim jeans", 
                new BigDecimal("89.99"), new BigDecimal("79.99"), 
                Set.of(clothing), 75, ProductStatus.ACTIVE);
                
        createProduct("Programming Guide", "Comprehensive programming reference", 
                new BigDecimal("49.99"), new BigDecimal("39.99"), 
                Set.of(books), 30, ProductStatus.ACTIVE);
                
        createProduct("Novel Collection", "Bestselling novels collection", 
                new BigDecimal("59.99"), new BigDecimal("49.99"), 
                Set.of(books), 20, ProductStatus.ACTIVE);
                
        createProduct("Decorative Vase", "Elegant ceramic vase", 
                new BigDecimal("39.99"), new BigDecimal("34.99"), 
                Set.of(homeDecor), 40, ProductStatus.ACTIVE);
                
        createProduct("Wall Art", "Modern wall painting", 
                new BigDecimal("149.99"), new BigDecimal("129.99"), 
                Set.of(homeDecor), 15, ProductStatus.ACTIVE);
                
        createProduct("Smart Watch", "Fitness tracking smartwatch", 
                new BigDecimal("249.99"), new BigDecimal("229.99"), 
                Set.of(electronics), 0, ProductStatus.OUT_OF_STOCK);
    }
    
    private Category createCategory(String name, String description) {
        Category category = Category.builder()
                .id(nextCategoryId)
                .name(name)
                .description(description)
                .build();
        categories.put(nextCategoryId, category);
        nextCategoryId++;
        return category;
    }
    
    private Product createProduct(String name, String description, BigDecimal basePrice, 
                                  BigDecimal currentPrice, Set<Category> categories, 
                                  Integer stockQuantity, ProductStatus status) {
        Product product = Product.builder()
                .id(nextProductId)
                .name(name)
                .description(description)
                .basePrice(basePrice)
                .currentPrice(currentPrice)
                .categories(categories)
                .stockQuantity(stockQuantity)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        products.put(nextProductId, product);
        nextProductId++;
        return product;
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product = Product.builder()
                    .id(nextProductId++)
                    .name(product.getName())
                    .description(product.getDescription())
                    .basePrice(product.getBasePrice())
                    .currentPrice(product.getCurrentPrice())
                    .categories(product.getCategories())
                    .imageUrl(product.getImageUrl())
                    .stockQuantity(product.getStockQuantity())
                    .status(product.getStatus())
                    .weight(product.getWeight())
                    .dimensions(product.getDimensions())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } else {
            // If product exists, update it with new values while preserving the ID
            Product existingProduct = products.get(product.getId());
            if (existingProduct != null) {
                product = Product.builder()
                        .id(existingProduct.getId())
                        .name(product.getName())
                        .description(product.getDescription())
                        .basePrice(product.getBasePrice())
                        .currentPrice(product.getCurrentPrice())
                        .categories(product.getCategories())
                        .imageUrl(product.getImageUrl())
                        .stockQuantity(product.getStockQuantity())
                        .status(product.getStatus())
                        .weight(product.getWeight())
                        .dimensions(product.getDimensions())
                        .createdAt(existingProduct.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .build();
            }
        }
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    @Override
    public void deleteById(Long id) {
        products.remove(id);
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        Category category = categories.get(categoryId);
        if (category == null) {
            return Collections.emptyList();
        }
        
        return products.values().stream()
                .filter(product -> product.getCategories() != null && 
                        product.getCategories().stream().anyMatch(c -> c.getId().equals(categoryId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNameContaining(String name) {
        if (name == null || name.isEmpty()) {
            return Collections.emptyList();
        }
        
        String lowerCaseName = name.toLowerCase();
        return products.values().stream()
                .filter(product -> product.getName().toLowerCase().contains(lowerCaseName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByDescriptionContaining(String description) {
        if (description == null || description.isEmpty()) {
            return Collections.emptyList();
        }
        
        String lowerCaseDesc = description.toLowerCase();
        return products.values().stream()
                .filter(product -> product.getDescription() != null && 
                        product.getDescription().toLowerCase().contains(lowerCaseDesc))
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> findByNameOrDescriptionContaining(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return Collections.emptyList();
        }
        
        String lowerCaseKeyword = keyword.toLowerCase();
        return products.values().stream()
                .filter(product -> 
                    product.getName().toLowerCase().contains(lowerCaseKeyword) || 
                    (product.getDescription() != null && 
                     product.getDescription().toLowerCase().contains(lowerCaseKeyword)))
                .collect(Collectors.toList());
    }
} 
