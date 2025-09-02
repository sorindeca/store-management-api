package com.sd.store.service;

import com.sd.store.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    Product addProduct(Product product);
    
    Optional<Product> findProductById(Long id);
    
    Optional<Product> findProductByName(String name);
    
    List<Product> findAllProducts();
    
    Page<Product> findAllProductsPaginated(Pageable pageable);
    
    List<Product> searchProductsByName(String name);
    
    Page<Product> searchProductsByNamePaginated(String name, Pageable pageable);
    
    Product updateProduct(Long id, Product product);
    
    Product changePrice(Long id, BigDecimal newPrice);
    
    void deleteProduct(Long id);
    
}
