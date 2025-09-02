package com.sd.store.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;


public record ProductDTO(
    Long id,
    
    @NotBlank(message = "Product name is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-_\\.]+$", message = "Product name can only contain letters, numbers, spaces, hyphens, underscores, and dots")
    String name,
    
    @NotBlank(message = "Product description is required")
    @Size(min = 10, max = 500, message = "Product description must be between 10 and 500 characters")
    String description,
    
    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price cannot exceed 999,999.99")
    @Digits(integer = 6, fraction = 2, message = "Price must have maximum 6 digits before decimal and 2 after")
    BigDecimal price,
    
    @NotNull(message = "Product quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Max(value = 999999, message = "Quantity cannot exceed 999,999")
    Integer quantity,
    
    @NotBlank(message = "Product category is required")
    @Pattern(regexp = "^[a-zA-Z\\s\\-]+$", message = "Category can only contain letters, spaces, and hyphens")
    String category,
    
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static ProductDTO fromProduct(com.sd.store.model.Product product) {
        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getQuantity(),
            product.getCategory(),
            product.getCreatedAt(),
            product.getUpdatedAt()
        );
    }
    

    public static ProductDTO forCreation(String name, String description, BigDecimal price, Integer quantity, String category) {
        return new ProductDTO(
            null,
            name,
            description,
            price,
            quantity,
            category,
            null,
            null
        );
    }
}
