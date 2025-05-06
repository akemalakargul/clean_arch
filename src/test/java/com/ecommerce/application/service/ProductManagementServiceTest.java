package com.ecommerce.application.service;

import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductStatus;
import com.ecommerce.domain.port.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductManagementServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductManagementServiceTestImpl productManagementService;

    private Product product1;
    private Product product2;
    private Category category;

    @BeforeEach
    void setUp() {
        // Initialize service with mocked repository
        productManagementService = new ProductManagementServiceTestImpl(productRepository);
        
        // Create test category
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronics category")
                .build();

        // Create test products
        product1 = Product.builder()
                .id(1L)
                .name("Smartphone")
                .description("Latest smartphone")
                .basePrice(new BigDecimal("699.99"))
                .currentPrice(new BigDecimal("649.99"))
                .stockQuantity(50)
                .status(ProductStatus.ACTIVE)
                .categories(Set.of(category))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Laptop")
                .description("High-end laptop")
                .basePrice(new BigDecimal("1299.99"))
                .currentPrice(new BigDecimal("1199.99"))
                .stockQuantity(25)
                .status(ProductStatus.ACTIVE)
                .categories(Set.of(category))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createProduct_shouldSaveAndReturnProduct() {
        // Arrange
        Product newProduct = Product.builder()
                .name("New Product")
                .description("New product description")
                .basePrice(new BigDecimal("99.99"))
                .currentPrice(new BigDecimal("89.99"))
                .stockQuantity(10)
                .status(ProductStatus.ACTIVE)
                .build();
        
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            return Product.builder()
                    .id(3L)
                    .name(savedProduct.getName())
                    .description(savedProduct.getDescription())
                    .basePrice(savedProduct.getBasePrice())
                    .currentPrice(savedProduct.getCurrentPrice())
                    .stockQuantity(savedProduct.getStockQuantity())
                    .status(savedProduct.getStatus())
                    .build();
        });

        // Act
        Product result = productManagementService.createProduct(newProduct);

        // Assert
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals("New Product", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_withExistingId_shouldUpdateAndReturnProduct() {
        // Arrange
        Long productId = 1L;
        Product updatedProduct = Product.builder()
                .name("Updated Smartphone")
                .description("Updated description")
                .basePrice(new BigDecimal("799.99"))
                .currentPrice(new BigDecimal("749.99"))
                .stockQuantity(40)
                .status(ProductStatus.ACTIVE)
                .build();
        
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            return savedProduct;
        });

        // Act
        Product result = productManagementService.updateProduct(productId, updatedProduct);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("Updated Smartphone", result.getName());
        assertEquals(new BigDecimal("749.99"), result.getCurrentPrice());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void updateProduct_withNonExistingId_shouldThrowException() {
        // Arrange
        Long nonExistingId = 999L;
        Product updatedProduct = Product.builder()
                .name("Updated Product")
                .description("Updated description")
                .build();
        
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            productManagementService.updateProduct(nonExistingId, updatedProduct)
        );
        verify(productRepository, times(1)).findById(nonExistingId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void deleteProduct_shouldCallRepositoryDelete() {
        // Arrange
        Long productId = 1L;

        // Act
        productManagementService.deleteProduct(productId);

        // Assert
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // Act
        List<Product> result = productManagementService.getAllProducts();

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedProducts, result);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_withExistingId_shouldReturnProduct() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        // Act
        Optional<Product> result = productManagementService.getProductById(productId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(product1, result.get());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void getProductById_withNonExistingId_shouldReturnEmpty() {
        // Arrange
        Long nonExistingId = 999L;
        when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productManagementService.getProductById(nonExistingId);

        // Assert
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findById(nonExistingId);
    }
} 