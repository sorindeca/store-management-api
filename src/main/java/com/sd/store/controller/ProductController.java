package com.sd.store.controller;

import com.sd.store.dto.ProductDTO;
import com.sd.store.model.Product;
import com.sd.store.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
@Tag(name = "Product Management", description = "API for product management")
public class ProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @Operation(
        summary = "Add a new product",
        description = "Creates a new product in the system. Requires ADMIN or MANAGER role."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid data - validation failed"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - missing required permissions"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public ResponseEntity<ProductDTO> addProduct(
            @Parameter(description = "Product data to add", required = true)
            @Valid @RequestBody ProductDTO productDTO) {
        logger.info("Request to add new product: {}", productDTO.name());
        
        Product savedProduct = productService.addProduct(convertToProduct(productDTO));
        return new ResponseEntity<>(ProductDTO.fromProduct(savedProduct), HttpStatus.CREATED);
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    @Operation(
        summary = "Get all products",
        description = "Returns the complete list of all products in the system"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Product list retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Product.class, type = "array")
            )
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access forbidden - missing required permissions"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Internal server error"
        )
    })
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("Request to get all products");
        List<Product> products = productService.findAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/paginated")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Page<Product>> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.info("Request to get all products with pagination: page={}, size={}, sortBy={}, sortDir={}", 
                   page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) 
                   ? Sort.by(sortBy).descending() 
                   : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productService.findAllProductsPaginated(pageable);
        
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.info("Request to get product by ID: {}", id);
        
        return productService.findProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        logger.info("Request to search products by name: {}", name);

        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search/paginated")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<Page<Product>> searchProductsPaginated(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        logger.info("Request to search products by name: {} with pagination: page={}, size={}, sortBy={}, sortDir={}", 
                   name, page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.DESC.name()) 
                   ? Sort.by(sortBy).descending() 
                   : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productService.searchProductsByNamePaginated(name, pageable);
        
        return ResponseEntity.ok(products);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        logger.info("Request to update product with ID: {}", id);
        
        Product updatedProduct = productService.updateProduct(id, convertToProduct(productDTO));
        return ResponseEntity.ok(ProductDTO.fromProduct(updatedProduct));
    }
    
    @PatchMapping("/{id}/price")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<Product> changePrice(@PathVariable Long id, @RequestParam BigDecimal price) {
        logger.info("Request to change price for product ID: {} to {}", id, price);

        Product updatedProduct = productService.changePrice(id, price);
        return ResponseEntity.ok(updatedProduct);
    }
    
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.info("Request to delete product with ID: {}", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or hasRole('EMPLOYEE')")
    public ResponseEntity<String> getProductStatus(@PathVariable Long id) {
        logger.info("Request to get product status for ID: {}", id);
        
        return productService.findProductById(id)
                .map(product -> {
                    String status = switch (product.getQuantity()) {
                        case 0 -> "OUT_OF_STOCK";
                        case 1, 2, 3, 4 -> "CRITICAL_STOCK";
                        case 5, 6, 7, 8, 9 -> "LOW_STOCK";
                        default -> product.getQuantity() > 50 ? "OVERSTOCKED" : "IN_STOCK";
                    };
                    
                                         return ResponseEntity.ok(status);
                 })
                 .orElse(ResponseEntity.notFound().build());
     }
     


     private Product convertToProduct(ProductDTO productDTO) {
         return new Product(
             productDTO.name(),
             productDTO.description(),
             productDTO.price(),
             productDTO.quantity(),
             productDTO.category()
         );
     }
 }
