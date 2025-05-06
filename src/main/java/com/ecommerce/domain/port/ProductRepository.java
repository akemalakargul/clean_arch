package com.ecommerce.domain.port;

import com.ecommerce.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    void deleteById(Long id);
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContaining(String name);

    List<Product> findByDescriptionContaining(String description);
    List<Product> findByNameOrDescriptionContaining(String keyword);

}
