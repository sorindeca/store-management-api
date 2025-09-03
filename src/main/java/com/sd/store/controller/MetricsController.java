package com.sd.store.controller;

import com.sd.store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {
    
    private final ProductRepository productRepository;
    
    @Value("${metrics.low-stock.threshold:5}")
    private int lowStockThreshold;
    
    @Value("${metrics.degraded.low-stock-threshold:10}")
    private int degradedLowStockThreshold;
    
    @Value("${metrics.down.out-of-stock-threshold:5}")
    private int downOutOfStockThreshold;
    
    public MetricsController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealthMetrics() {
        Map<String, Object> health = new HashMap<>();
        
        try {
            long totalProducts = productRepository.count();
            long lowStockProducts = productRepository.countByQuantityLessThan(lowStockThreshold);
            long outOfStockProducts = productRepository.countOutOfStockProducts();
            
            long inStockProducts = totalProducts - outOfStockProducts;
            double stockAvailabilityRate = totalProducts > 0 ? 
                (double) inStockProducts / totalProducts * 100 : 0;
            
            health.put("timestamp", LocalDateTime.now());
            health.put("status", "UP");
            health.put("totalProducts", totalProducts);
            health.put("inStockProducts", inStockProducts);
            health.put("lowStockProducts", lowStockProducts);
            health.put("outOfStockProducts", outOfStockProducts);
            health.put("stockAvailabilityRate", Math.round(stockAvailabilityRate * 100.0) / 100.0);
            health.put("lowStockThreshold", lowStockThreshold);
            
            if (outOfStockProducts > downOutOfStockThreshold) {
                health.put("status", "DOWN");
                health.put("message", "Too many products out of stock: " + outOfStockProducts);
            } else if (lowStockProducts > degradedLowStockThreshold) {
                health.put("status", "DEGRADED");
                health.put("message", "Too many products with low stock: " + lowStockProducts);
            } else {
                health.put("message", "All systems operational");
            }
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            health.put("timestamp", LocalDateTime.now());
            health.put("status", "DOWN");
            health.put("message", "Error retrieving metrics: " + e.getMessage());
            return ResponseEntity.status(500).body(health);
        }
    }
}
