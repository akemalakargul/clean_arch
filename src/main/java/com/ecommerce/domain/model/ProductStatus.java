package com.ecommerce.domain.model;

/**
 * Represents the current status of a product in the e-commerce system.
 */
public enum ProductStatus {
    /**
     * Product is active and available for purchase
     */
    ACTIVE,
    
    /**
     * Product has been discontinued and is no longer available
     */
    DISCONTINUED,
    
    /**
     * Product is temporarily out of stock but may become available later
     */
    OUT_OF_STOCK
} 