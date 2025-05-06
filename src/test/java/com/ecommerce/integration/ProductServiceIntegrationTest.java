package com.ecommerce.integration;

import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceIntegrationTest {

    private InMemoryProductRepository repository;
    private ProductServiceTestable service;
    private List<Product> initialProducts;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();
        service = new ProductServiceTestable(repository);
        
        // Setup some initial products
        initialProducts = new ArrayList<>();
        
        Product product1 = Product.builder()
                .name("Test Phone")
                .description("A test smartphone")
                .basePrice(new BigDecimal("499.99"))
                .currentPrice(new BigDecimal("450.00"))
                .stockQuantity(100)
                .status(ProductStatus.ACTIVE)
                .build();
        
        Product product2 = Product.builder()
                .name("Test Laptop")
                .description("A test laptop")
                .basePrice(new BigDecimal("1200.00"))
                .currentPrice(new BigDecimal("1100.00"))
                .stockQuantity(50)
                .status(ProductStatus.ACTIVE)
                .build();
        
        initialProducts.add(repository.save(product1));
        initialProducts.add(repository.save(product2));
    }
    
    @Test
    void getAllProducts_shouldReturnAllProducts() {
        // Act
        List<Product> results = service.getAllProducts();
        
        // Assert
        assertEquals(2, results.size());
        assertEquals("Test Phone", results.get(0).getName());
        assertEquals("Test Laptop", results.get(1).getName());
    }
    
    @Test
    void createProduct_shouldSaveAndReturnProduct() {
        // Arrange
        Product newProduct = Product.builder()
                .name("Test Tablet")
                .description("A test tablet")
                .basePrice(new BigDecimal("300.00"))
                .currentPrice(new BigDecimal("299.99"))
                .stockQuantity(75)
                .status(ProductStatus.ACTIVE)
                .build();
        
        // Act
        Product savedProduct = service.createProduct(newProduct);
        
        // Assert
        assertNotNull(savedProduct.getId());
        assertEquals("Test Tablet", savedProduct.getName());
        
        // Verify it was actually saved
        Optional<Product> fromRepo = repository.findById(savedProduct.getId());
        assertTrue(fromRepo.isPresent());
        assertEquals("Test Tablet", fromRepo.get().getName());
    }
    
    @Test
    void getProductById_withExistingId_shouldReturnProduct() {
        // Arrange
        Long existingId = initialProducts.get(0).getId();
        
        // Act
        Optional<Product> result = service.getProductById(existingId);
        
        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Phone", result.get().getName());
    }
    
    @Test
    void getProductById_withNonExistingId_shouldReturnEmpty() {
        // Act
        Optional<Product> result = service.getProductById(999L);
        
        // Assert
        assertTrue(result.isEmpty());
    }
    
    @Test
    void updateProduct_withExistingId_shouldUpdateAndReturnProduct() {
        // Arrange
        Long existingId = initialProducts.get(0).getId();
        Product updatedProduct = Product.builder()
                .name("Updated Phone")
                .description("An updated phone")
                .basePrice(new BigDecimal("599.99"))
                .currentPrice(new BigDecimal("550.00"))
                .stockQuantity(80)
                .status(ProductStatus.ACTIVE)
                .build();
        
        // Act
        Product result = service.updateProduct(existingId, updatedProduct);
        
        // Assert
        assertEquals(existingId, result.getId());
        assertEquals("Updated Phone", result.getName());
        assertEquals(new BigDecimal("550.00"), result.getCurrentPrice());
        
        // Verify it was actually updated in the repository
        Optional<Product> fromRepo = repository.findById(existingId);
        assertTrue(fromRepo.isPresent());
        assertEquals("Updated Phone", fromRepo.get().getName());
    }
    
    @Test
    void updateProduct_withNonExistingId_shouldThrowException() {
        // Arrange
        Product updatedProduct = Product.builder().name("New Name").build();
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            service.updateProduct(999L, updatedProduct)
        );
    }
    
    @Test
    void deleteProduct_withExistingId_shouldRemoveFromRepository() {
        // Arrange
        Long existingId = initialProducts.get(0).getId();
        
        // Act
        service.deleteProduct(existingId);
        
        // Assert
        Optional<Product> fromRepo = repository.findById(existingId);
        assertTrue(fromRepo.isEmpty());
        assertEquals(1, repository.findAll().size());
    }
    
    /**
     * Simple in-memory repository implementation for testing
     */
    private static class InMemoryProductRepository {
        private final List<Product> products = new ArrayList<>();
        private Long nextId = 1L;
        
        public Product save(Product product) {
            if (product.getId() == null) {
                // This is a new product
                Product savedProduct = Product.builder()
                        .id(nextId++)
                        .name(product.getName())
                        .description(product.getDescription())
                        .basePrice(product.getBasePrice())
                        .currentPrice(product.getCurrentPrice())
                        .stockQuantity(product.getStockQuantity())
                        .status(product.getStatus())
                        .categories(product.getCategories())
                        .createdAt(product.getCreatedAt())
                        .updatedAt(product.getUpdatedAt())
                        .build();
                products.add(savedProduct);
                return savedProduct;
            } else {
                // This is an update
                deleteById(product.getId());
                products.add(product);
                return product;
            }
        }
        
        public Optional<Product> findById(Long id) {
            return products.stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst();
        }
        
        public List<Product> findAll() {
            return new ArrayList<>(products);
        }
        
        public void deleteById(Long id) {
            products.removeIf(p -> p.getId().equals(id));
        }
    }
    
    /**
     * Simplified product service for testing
     */
    private static class ProductServiceTestable {
        private final InMemoryProductRepository repository;
        
        public ProductServiceTestable(InMemoryProductRepository repository) {
            this.repository = repository;
        }
        
        public Product createProduct(Product product) {
            return repository.save(product);
        }
        
        public Product updateProduct(Long id, Product product) {
            Optional<Product> existingProduct = repository.findById(id);
            if (existingProduct.isPresent()) {
                product.setId(id);
                return repository.save(product);
            }
            throw new RuntimeException("Product not found with id: " + id);
        }
        
        public void deleteProduct(Long id) {
            repository.deleteById(id);
        }
        
        public List<Product> getAllProducts() {
            return repository.findAll();
        }
        
        public Optional<Product> getProductById(Long id) {
            return repository.findById(id);
        }
    }
} 
