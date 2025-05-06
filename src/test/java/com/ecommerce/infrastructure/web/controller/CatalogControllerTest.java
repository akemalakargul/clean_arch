package com.ecommerce.infrastructure.web.controller;

import com.ecommerce.application.service.CatalogBrowsingService;
import com.ecommerce.domain.model.Category;
import com.ecommerce.domain.model.Product;
import com.ecommerce.domain.model.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogController.class)
public class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CatalogBrowsingService catalogService;

    private Product product1;
    private Product product2;
    private List<Product> productList;

    @BeforeEach
    void setUp() {
        // Create test category
        Category electronics = Category.builder()
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
                .categories(Set.of(electronics))
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
                .categories(Set.of(electronics))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        productList = Arrays.asList(product1, product2);
    }

    @Test
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        // Arrange
        when(catalogService.getAllActiveProducts()).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/api/catalog")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Smartphone")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Laptop")));
    }

    @Test
    void getProductsByCategory_shouldReturnFilteredProducts() throws Exception {
        // Arrange
        when(catalogService.getProductsByCategory(1L)).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/api/catalog/category/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Smartphone")))
                .andExpect(jsonPath("$[0].currentPrice", is(649.99)));
    }

    @Test
    void searchProducts_shouldReturnMatchingProducts() throws Exception {
        // Arrange
        when(catalogService.searchProducts("smart")).thenReturn(List.of(product1));

        // Act & Assert
        mockMvc.perform(get("/api/catalog/search")
                .param("keyword", "smart")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Smartphone")));
    }

    @Test
    void sortByPriceAscending_shouldReturnSortedProducts() throws Exception {
        // Arrange
        List<Product> sortedProducts = Arrays.asList(product1, product2); // Already arranged by price
        when(catalogService.getAllActiveProducts()).thenReturn(productList);
        when(catalogService.sortProductsByPriceAsc(any())).thenReturn(sortedProducts);

        // Act & Assert
        mockMvc.perform(get("/api/catalog/sort/price-asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].currentPrice", is(649.99)))
                .andExpect(jsonPath("$[1].currentPrice", is(1199.99)));
    }

    @Test
    void filterByPriceRange_shouldReturnFilteredProducts() throws Exception {
        // Arrange
        when(catalogService.getAllActiveProducts()).thenReturn(productList);
        when(catalogService.filterByPriceRange(any(), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(List.of(product1));

        // Act & Assert
        mockMvc.perform(get("/api/catalog/filter/price")
                .param("minPrice", "500")
                .param("maxPrice", "700")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Smartphone")))
                .andExpect(jsonPath("$[0].currentPrice", is(649.99)));
    }

    @Test
    void browseProducts_shouldCombineFiltersAndSort() throws Exception {
        // Arrange
        when(catalogService.getProductsByCategory(1L)).thenReturn(productList);
        when(catalogService.filterByPriceRange(any(), any(BigDecimal.class), any(BigDecimal.class)))
                .thenReturn(List.of(product1));
        when(catalogService.sortProductsByPriceAsc(any())).thenReturn(List.of(product1));

        // Act & Assert
        mockMvc.perform(get("/api/catalog/browse")
                .param("categoryId", "1")
                .param("minPrice", "500")
                .param("maxPrice", "700")
                .param("sortBy", "price_asc")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Smartphone")));
    }
} 