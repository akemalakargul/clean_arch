package com.ecommerce.integration;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for catalog browsing functionality
 */
public class CatalogBrowsingIntegrationTest {

    private InMemoryProductRepository repository;
    private CatalogServiceTestable service;
    private List<Product> allProducts;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();
        service = new CatalogServiceTestable(repository);
        
        allProducts = new ArrayList<>();
        
        // Add some active products
        Product phone = Product.builder()
                .name("Budget Phone")
                .description("Affordable smartphone")
                .basePrice(new BigDecimal("299.99"))
                .currentPrice(new BigDecimal("279.99"))
                .stockQuantity(100)
                .status(ProductStatus.ACTIVE)
                .categoryId(1L) // Electronics
                .build();
        
        Product laptop = Product.builder()
                .name("Gaming Laptop")
                .description("High-performance gaming laptop")
                .basePrice(new BigDecimal("1499.99"))
                .currentPrice(new BigDecimal("1399.99"))
                .stockQuantity(50)
                .status(ProductStatus.ACTIVE)
                .categoryId(1L) // Electronics
                .build();
        
        Product book = Product.builder()
                .name("Programming Book")
                .description("Learn Java programming")
                .basePrice(new BigDecimal("49.99"))
                .currentPrice(new BigDecimal("39.99"))
                .stockQuantity(200)
                .status(ProductStatus.ACTIVE)
                .categoryId(2L) // Books
                .build();
        
        // Add a discontinued product
        Product oldPhone = Product.builder()
                .name("Old Phone")
                .description("Discontinued model")
                .basePrice(new BigDecimal("199.99"))
                .currentPrice(new BigDecimal("99.99"))
                .stockQuantity(10)
                .status(ProductStatus.DISCONTINUED)
                .categoryId(1L) // Electronics
                .build();
        
        allProducts.add(repository.save(phone));
        allProducts.add(repository.save(laptop));
        allProducts.add(repository.save(book));
        allProducts.add(repository.save(oldPhone));
    }
    
    @Test
    void getAllActiveProducts_shouldReturnOnlyActiveProducts() {
        // Act
        List<Product> activeProducts = service.getAllActiveProducts();
        
        // Assert
        assertEquals(3, activeProducts.size());
        assertTrue(activeProducts.stream().allMatch(p -> p.getStatus() == ProductStatus.ACTIVE));
    }
    
    @Test
    void getProductsByCategory_shouldReturnActiveProductsInCategory() {
        // Act
        List<Product> electronicsProducts = service.getProductsByCategory(1L);
        List<Product> bookProducts = service.getProductsByCategory(2L);
        
        // Assert
        assertEquals(2, electronicsProducts.size());
        assertEquals(1, bookProducts.size());
        assertTrue(electronicsProducts.stream().allMatch(p -> p.getCategoryId() == 1L));
        assertTrue(bookProducts.stream().allMatch(p -> p.getCategoryId() == 2L));
    }
    
    @Test
    void searchProducts_shouldFindProductsMatchingKeyword() {
        // Act
        List<Product> phoneResults = service.searchProducts("phone");
        List<Product> programmingResults = service.searchProducts("programming");
        List<Product> noMatchResults = service.searchProducts("tablet");
        
        // Assert
        assertEquals(1, phoneResults.size());
        assertEquals("Budget Phone", phoneResults.get(0).getName());
        
        assertEquals(1, programmingResults.size());
        assertEquals("Programming Book", programmingResults.get(0).getName());
        
        assertEquals(0, noMatchResults.size());
    }
    
    @Test
    void sortProductsByPriceAsc_shouldSortProductsByPriceAscending() {
        // Arrange
        List<Product> products = service.getAllActiveProducts();
        
        // Act
        List<Product> sortedProducts = service.sortProductsByPriceAsc(products);
        
        // Assert
        assertEquals(3, sortedProducts.size());
        assertEquals("Programming Book", sortedProducts.get(0).getName()); // Cheapest
        assertEquals("Budget Phone", sortedProducts.get(1).getName()); // Middle
        assertEquals("Gaming Laptop", sortedProducts.get(2).getName()); // Most expensive
    }
    
    @Test
    void sortProductsByPriceDesc_shouldSortProductsByPriceDescending() {
        // Arrange
        List<Product> products = service.getAllActiveProducts();
        
        // Act
        List<Product> sortedProducts = service.sortProductsByPriceDesc(products);
        
        // Assert
        assertEquals(3, sortedProducts.size());
        assertEquals("Gaming Laptop", sortedProducts.get(0).getName()); // Most expensive
        assertEquals("Budget Phone", sortedProducts.get(1).getName()); // Middle
        assertEquals("Programming Book", sortedProducts.get(2).getName()); // Cheapest
    }
    
    @Test
    void filterByPriceRange_shouldFilterProductsWithinPriceRange() {
        // Arrange
        List<Product> products = service.getAllActiveProducts();
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("300.00");
        
        // Act
        List<Product> filteredProducts = service.filterByPriceRange(products, minPrice, maxPrice);
        
        // Assert
        assertEquals(1, filteredProducts.size());
        assertEquals("Budget Phone", filteredProducts.get(0).getName());
    }
    
    /**
     * Simple in-memory product repository for testing
     */
    private static class InMemoryProductRepository {
        private List<Product> products = new ArrayList<>();
        private Long nextId = 1L;
        
        public Product save(Product product) {
            if (product.getId() == null) {
                Product savedProduct = Product.builder()
                        .id(nextId++)
                        .name(product.getName())
                        .description(product.getDescription())
                        .basePrice(product.getBasePrice())
                        .currentPrice(product.getCurrentPrice())
                        .stockQuantity(product.getStockQuantity())
                        .status(product.getStatus())
                        .categoryId(product.getCategoryId())
                        .build();
                products.add(savedProduct);
                return savedProduct;
            } else {
                products.removeIf(p -> p.getId().equals(product.getId()));
                products.add(product);
                return product;
            }
        }
        
        public List<Product> findAll() {
            return new ArrayList<>(products);
        }
        
        public List<Product> findByCategoryId(Long categoryId) {
            return products.stream()
                    .filter(p -> p.getCategoryId() != null && p.getCategoryId().equals(categoryId))
                    .collect(Collectors.toList());
        }
        
        public List<Product> findByNameOrDescriptionContaining(String keyword) {
            keyword = keyword.toLowerCase();
            final String query = keyword;
            return products.stream()
                    .filter(p -> 
                        (p.getName() != null && p.getName().toLowerCase().contains(query)) || 
                        (p.getDescription() != null && p.getDescription().toLowerCase().contains(query)))
                    .collect(Collectors.toList());
        }
    }
    
    /**
     * Simple product model extension for testing
     */
    private static class Product {
        private Long id;
        private String name;
        private String description;
        private BigDecimal basePrice;
        private BigDecimal currentPrice;
        private Integer stockQuantity;
        private ProductStatus status;
        private Long categoryId;
        
        private Product(Builder builder) {
            this.id = builder.id;
            this.name = builder.name;
            this.description = builder.description;
            this.basePrice = builder.basePrice;
            this.currentPrice = builder.currentPrice;
            this.stockQuantity = builder.stockQuantity;
            this.status = builder.status;
            this.categoryId = builder.categoryId;
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public BigDecimal getBasePrice() { return basePrice; }
        public BigDecimal getCurrentPrice() { return currentPrice; }
        public Integer getStockQuantity() { return stockQuantity; }
        public ProductStatus getStatus() { return status; }
        public Long getCategoryId() { return categoryId; }
        
        public static class Builder {
            private Long id;
            private String name;
            private String description;
            private BigDecimal basePrice;
            private BigDecimal currentPrice;
            private Integer stockQuantity;
            private ProductStatus status;
            private Long categoryId;
            
            public Builder id(Long id) { this.id = id; return this; }
            public Builder name(String name) { this.name = name; return this; }
            public Builder description(String description) { this.description = description; return this; }
            public Builder basePrice(BigDecimal basePrice) { this.basePrice = basePrice; return this; }
            public Builder currentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; return this; }
            public Builder stockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; return this; }
            public Builder status(ProductStatus status) { this.status = status; return this; }
            public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
            
            public Product build() {
                return new Product(this);
            }
        }
    }
    
    /**
     * Simple catalog service for testing
     */
    private static class CatalogServiceTestable {
        private final InMemoryProductRepository repository;
        
        public CatalogServiceTestable(InMemoryProductRepository repository) {
            this.repository = repository;
        }
        
        public List<Product> getAllActiveProducts() {
            return repository.findAll().stream()
                    .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                    .collect(Collectors.toList());
        }
        
        public List<Product> getProductsByCategory(Long categoryId) {
            return repository.findByCategoryId(categoryId).stream()
                    .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                    .collect(Collectors.toList());
        }
        
        public List<Product> searchProducts(String keyword) {
            return repository.findByNameOrDescriptionContaining(keyword).stream()
                    .filter(p -> p.getStatus() == ProductStatus.ACTIVE)
                    .collect(Collectors.toList());
        }
        
        public List<Product> sortProductsByPriceAsc(List<Product> products) {
            return products.stream()
                    .sorted((p1, p2) -> p1.getCurrentPrice().compareTo(p2.getCurrentPrice()))
                    .collect(Collectors.toList());
        }
        
        public List<Product> sortProductsByPriceDesc(List<Product> products) {
            return products.stream()
                    .sorted((p1, p2) -> p2.getCurrentPrice().compareTo(p1.getCurrentPrice()))
                    .collect(Collectors.toList());
        }
        
        public List<Product> filterByPriceRange(List<Product> products, BigDecimal minPrice, BigDecimal maxPrice) {
            return products.stream()
                    .filter(p -> {
                        BigDecimal price = p.getCurrentPrice();
                        return (minPrice == null || price.compareTo(minPrice) >= 0) &&
                               (maxPrice == null || price.compareTo(maxPrice) <= 0);
                    })
                    .collect(Collectors.toList());
        }
    }
} 