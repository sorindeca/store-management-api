package com.sd.store.service;

import com.sd.store.model.Product;
import com.sd.store.repository.ProductRepository;
import com.sd.store.service.impl.ProductServiceImpl;
import com.sd.store.exception.ProductNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setQuantity(10);
        testProduct.setCategory("Electronics");
    }

    @Test
    void givenValidProduct_whenAddingProduct_thenProductIsSavedSuccessfully() {
        Product productToAdd = new Product();
        productToAdd.setName("New Product");
        productToAdd.setDescription("New Description");
        productToAdd.setPrice(new BigDecimal("199.99"));
        productToAdd.setQuantity(20);
        productToAdd.setCategory("Gaming");

        when(productRepository.findByName("New Product")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(productToAdd);

        Product result = productService.addProduct(productToAdd);

        assertNotNull(result);
        assertEquals("New Product", result.getName());
        assertEquals(new BigDecimal("199.99"), result.getPrice());
        
        verify(productRepository, times(1)).findByName("New Product");
        verify(productRepository, times(1)).save(productToAdd);
    }

    @Test
    void givenProductWithExistingName_whenAddingProduct_thenExceptionIsThrown() {
        Product productToAdd = new Product();
        productToAdd.setName("Test Product");
        
        when(productRepository.findByName("Test Product")).thenReturn(Optional.of(testProduct));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.addProduct(productToAdd);
        });
        
        assertEquals("Product with name 'Test Product' already exists", exception.getMessage());
        verify(productRepository, times(1)).findByName("Test Product");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void givenExistingProductId_whenFindingProduct_thenProductIsReturned() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Optional<Product> result = productService.findProductById(1L);

        assertTrue(result.isPresent());
        Product foundProduct = result.get();
        assertEquals(1L, foundProduct.getId());
        assertEquals("Test Product", foundProduct.getName());
        assertEquals(new BigDecimal("99.99"), foundProduct.getPrice());
        
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void givenNonExistentProductId_whenFindingProduct_thenEmptyIsReturned() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<Product> result = productService.findProductById(999L);

        assertFalse(result.isPresent());
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void givenValidUpdateData_whenUpdatingProduct_thenAllFieldsAreUpdated() {
        Product updateData = new Product();
        updateData.setName("Updated Product");
        updateData.setDescription("Updated Description");
        updateData.setPrice(new BigDecimal("149.99"));
        updateData.setQuantity(15);
        updateData.setCategory("Updated Electronics");

        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Updated Product");
        updatedProduct.setDescription("Updated Description");
        updatedProduct.setPrice(new BigDecimal("149.99"));
        updatedProduct.setQuantity(15);
        updatedProduct.setCategory("Updated Electronics");
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updateData);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Product", result.getName());
        assertEquals(new BigDecimal("149.99"), result.getPrice());
        
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void givenInvalidUpdateData_whenUpdatingProduct_thenExceptionIsThrown() {
        Product invalidUpdateData = new Product();
        invalidUpdateData.setName(""); 
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(1L, invalidUpdateData);
        });
        
        assertEquals("Invalid product data provided", exception.getMessage());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void givenNonExistentProductId_whenUpdatingProduct_thenProductNotFoundExceptionIsThrown() {
        Product updateData = new Product();
        updateData.setName("Updated Product");
        
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(999L, updateData);
        });
        
        assertEquals("Product not found with ID: 999", exception.getMessage());
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }
}
