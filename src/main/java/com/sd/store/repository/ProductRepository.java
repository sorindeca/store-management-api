package com.sd.store.repository;

import com.sd.store.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Optional<Product> findByName(String name);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity < :threshold")
    long countByQuantityLessThan(@Param("threshold") Integer threshold);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.quantity = 0")
    long countOutOfStockProducts();
}
