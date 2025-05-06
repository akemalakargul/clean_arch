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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CatalogBrowsingServiceTest {

    @Mock
    private ProductRepository productRepository;

    private CatalogBrowsingServiceTestImpl catalogBrowsingService;

    private Product product1;
    private Product product2;
    private Product product3;
    private List<Product> allProducts;
    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        catalogBrowsingService = new CatalogBrowsingServiceTestImpl(productRepository);
        
        category1 = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronics category")
                .build();

        category2 = Category.builder()
                .id(2L)
                .name("Books")
                .description("Books category")
                .build();

        product1 = Product.builder()
                .id(1L)
                .name("Smartphone")
                .description("Latest smartphone")
                .basePrice(new BigDecimal("699.99"))
                .currentPrice(new BigDecimal("649.99"))
                .stockQuantity(50)
                .status(ProductStatus.ACTIVE)
                .categories(Set.of(category1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        product2 = Product.builder()
                .id(2L)
                .name("Programming Book")
                .description("Java programming book")
                .basePrice(new BigDecimal("49.99"))
                .currentPrice(new BigDecimal("39.99"))
                .stockQuantity(100)
                .status(ProductStatus.ACTIVE)
                .categories(Set.of(category2))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        product3 = Product.builder()
                .id(3L)
                .name("Old Phone")
                .description("Discontinued phone model")
                .basePrice(new BigDecimal("299.99"))
                .currentPrice(new BigDecimal("199.99"))
                .stockQuantity(10)
                .status(ProductStatus.DISCONTINUED)
                .categories(Set.of(category1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        allProducts = Arrays.asList(product1, product2, product3);
    }

    @Test
    void getAllActiveProducts_shouldReturnOnlyActiveProducts() {
        when(productRepository.findAll()).thenReturn(allProducts);

        List<Product> activeProducts = catalogBrowsingService.getAllActiveProducts();

        assertEquals(2, activeProducts.size());
        assertTrue(activeProducts.stream().allMatch(p -> p.getStatus() == ProductStatus.ACTIVE));
        assertTrue(activeProducts.contains(product1));
        assertTrue(activeProducts.contains(product2));
    }

    @Test
    void getProductsByCategory_shouldReturnProductsForSpecificCategory() {
        // Arrange
        when(productRepository.findByCategoryId(1L)).thenReturn(List.of(product1, product3));

        // Act
        List<Product> categoryProducts = catalogBrowsingService.getProductsByCategory(1L);

        // Assert
        assertEquals(1, categoryProducts.size());
        assertEquals(ProductStatus.ACTIVE, categoryProducts.get(0).getStatus());
        assertEquals("Smartphone", categoryProducts.get(0).getName());
    }

    @Test
    void searchProducts_shouldFindProductsMatchingKeyword() {
        // Arrange
        when(productRepository.findByNameOrDescriptionContaining("phone")).thenReturn(List.of(product1, product3));

        // Act
        List<Product> searchResults = catalogBrowsingService.searchProducts("phone");

        // Assert
        assertEquals(1, searchResults.size());
        assertEquals("Smartphone", searchResults.get(0).getName());
        assertEquals(ProductStatus.ACTIVE, searchResults.get(0).getStatus());
    }

    @Test
    void sortProductsByPriceAsc_shouldSortProductsAscending() {
        // Arrange
        List<Product> products = Arrays.asList(product1, product2);

        // Act
        List<Product> sortedProducts = catalogBrowsingService.sortProductsByPriceAsc(products);

        // Assert
        assertEquals(2, sortedProducts.size());
        assertEquals(product2.getId(), sortedProducts.get(0).getId()); // Programming Book is cheaper
        assertEquals(product1.getId(), sortedProducts.get(1).getId()); // Smartphone is more expensive
    }

    @Test
    void sortProductsByPriceDesc_shouldSortProductsDescending() {
        // Arrange
        List<Product> products = Arrays.asList(product2, product1);

        // Act
        List<Product> sortedProducts = catalogBrowsingService.sortProductsByPriceDesc(products);

        // Assert
        assertEquals(2, sortedProducts.size());
        assertEquals(product1.getId(), sortedProducts.get(0).getId()); // Smartphone is more expensive
        assertEquals(product2.getId(), sortedProducts.get(1).getId()); // Programming Book is cheaper
    }

    @Test
    void filterByPriceRange_shouldFilterProductsWithinRange() {
        // Arrange
        List<Product> products = Arrays.asList(product1, product2);
        BigDecimal minPrice = new BigDecimal("40.00");
        BigDecimal maxPrice = new BigDecimal("500.00");

        // Act
        List<Product> filteredProducts = catalogBrowsingService.filterByPriceRange(products, minPrice, maxPrice);

        // Assert
        assertEquals(1, filteredProducts.size());
        assertEquals(product2.getId(), filteredProducts.get(0).getId()); // Only the book is in this price range
    }
} 
