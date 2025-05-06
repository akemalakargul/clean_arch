package com.ecommerce.infrastructure.web.controller;

import com.ecommerce.application.service.CatalogBrowsingService;
import com.ecommerce.domain.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CatalogBrowsingService catalogService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(catalogService.getAllActiveProducts());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(catalogService.getProductsByCategory(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(catalogService.searchProducts(keyword));
    }

    @GetMapping("/sort/price-asc")
    public ResponseEntity<List<Product>> sortByPriceAscending() {
        List<Product> products = catalogService.getAllActiveProducts();
        return ResponseEntity.ok(catalogService.sortProductsByPriceAsc(products));
    }

    @GetMapping("/sort/price-desc")
    public ResponseEntity<List<Product>> sortByPriceDescending() {
        List<Product> products = catalogService.getAllActiveProducts();
        return ResponseEntity.ok(catalogService.sortProductsByPriceDesc(products));
    }

    @GetMapping("/filter/price")
    public ResponseEntity<List<Product>> filterByPriceRange(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        List<Product> products = catalogService.getAllActiveProducts();
        return ResponseEntity.ok(catalogService.filterByPriceRange(products, minPrice, maxPrice));
    }

    @GetMapping("/browse")
    public ResponseEntity<List<Product>> browseProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false, defaultValue = "default") String sortBy) {
        
        List<Product> products;
        
        if (categoryId != null) {
            products = catalogService.getProductsByCategory(categoryId);
        } else {
            products = catalogService.getAllActiveProducts();
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            products = products.stream()
                .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()) || 
                        (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .toList();
        }
        
        products = catalogService.filterByPriceRange(products, minPrice, maxPrice);
        
        if ("price_asc".equals(sortBy)) {
            products = catalogService.sortProductsByPriceAsc(products);
        } else if ("price_desc".equals(sortBy)) {
            products = catalogService.sortProductsByPriceDesc(products);
        }
        
        return ResponseEntity.ok(products);
    }
} 
