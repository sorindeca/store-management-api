package com.sd.store.service.impl;

import com.sd.store.exception.ProductNotFoundException;
import com.sd.store.model.Product;
import com.sd.store.repository.ProductRepository;
import com.sd.store.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @Override
    public Product addProduct(Product product) {
        logger.info("Adding new product: {}", product.getName());
        
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new IllegalArgumentException("Product with name '" + product.getName() + "' already exists");
        }
        
        Product savedProduct = productRepository.save(product);
        logger.info("Product added successfully with ID: {}", savedProduct.getId());
        return savedProduct;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findProductById(Long id) {
        logger.debug("Finding product by ID: {}", id);
        return productRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findProductByName(String name) {
        logger.debug("Finding product by name: {}", name);
        return productRepository.findByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> findAllProducts() {
        logger.debug("Finding all products");
        return productRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProductsByName(String name) {
        logger.debug("Searching products by name containing: {}", name);
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    public Product updateProduct(Long id, Product product) {
        logger.info("Updating product with ID: {}", id);
        
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        
        if (product.getName() != null && !product.getName().trim().isEmpty()) {
            existingProduct.setName(product.getName());
            existingProduct.setDescription(product.getDescription());
            existingProduct.setPrice(product.getPrice());
            existingProduct.setQuantity(product.getQuantity());
            existingProduct.setCategory(product.getCategory());
        } else {
            throw new IllegalArgumentException("Invalid product data provided");
        }
        
        Product updatedProduct = productRepository.save(existingProduct);
        logger.info("Product updated successfully: {}", updatedProduct.getId());
        return updatedProduct;
    }
    
    @Override
    public Product changePrice(Long id, BigDecimal newPrice) {
        logger.info("Changing price for product ID: {} to {}", id, newPrice);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
        
        BigDecimal oldPrice = product.getPrice();
        product.setPrice(newPrice);
        
        Product updatedProduct = productRepository.save(product);
        logger.info("Price changed from {} to {} for product: {}", oldPrice, newPrice, product.getName());
        return updatedProduct;
    }
    
    @Override
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with ID: " + id);
        }
        
        productRepository.deleteById(id);
        logger.info("Product deleted successfully with ID: {}", id);
    }
    
}
