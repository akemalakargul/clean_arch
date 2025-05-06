package com.ecommerce.infrastructure.persistence;

import com.ecommerce.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, Long> {
    @Query("SELECT p FROM ProductEntity p JOIN p.categories c WHERE c.id = :categoryId")
    List<ProductEntity> findByCategoryId(@Param("categoryId") Long categoryId);

    List<ProductEntity> findByNameContainingIgnoreCase(String name);
    
    List<ProductEntity> findByDescriptionContainingIgnoreCase(String description);
    
    List<ProductEntity> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
} 
