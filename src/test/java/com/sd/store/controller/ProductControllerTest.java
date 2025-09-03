package com.sd.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sd.store.dto.ProductDTO;
import com.sd.store.model.Product;
import com.sd.store.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;
    private ProductDTO testProductDTO;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setQuantity(10);
        testProduct.setCategory("Electronics");

        testProductDTO = ProductDTO.forCreation(
            testProduct.getName(),
            testProduct.getDescription(),
            testProduct.getPrice(),
            testProduct.getQuantity(),
            testProduct.getCategory()
        );
    }

   @Test
   @WithMockUser(roles = "ADMIN")
   void givenValidProductData_whenAdminAddsProduct_thenProductIsCreated() throws Exception {
       when(productService.addProduct(any(Product.class))).thenReturn(testProduct);

       mockMvc.perform(post("/api/products")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(testProductDTO))
               .with(csrf()))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(testProduct.getId()))
               .andExpect(jsonPath("$.name").value(testProduct.getName()))
               .andExpect(jsonPath("$.price").value(testProduct.getPrice().doubleValue()));

       verify(productService, times(1)).addProduct(any(Product.class));
   }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void givenEmployeeRole_whenAddingProduct_thenAccessIsForbidden() throws Exception {
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testProductDTO)))
                .andExpect(status().isForbidden());

        verify(productService, never()).addProduct(any(Product.class));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void givenExistingProductId_whenGettingProduct_thenProductIsReturned() throws Exception {
        when(productService.findProductById(1L)).thenReturn(Optional.of(testProduct));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testProduct.getId()))
                .andExpect(jsonPath("$.name").value(testProduct.getName()));

        verify(productService, times(1)).findProductById(1L);
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    void givenNonExistentProductId_whenGettingProduct_thenNotFoundIsReturned() throws Exception {
        when(productService.findProductById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).findProductById(999L);
    }

   @Test
   @WithMockUser(roles = "ADMIN")
   void givenValidUpdateData_whenAdminUpdatesProduct_thenProductIsUpdated() throws Exception {
       ProductDTO updatedProductDTO = ProductDTO.forCreation(
           "Updated Product", "Updated Description",
           new BigDecimal("149.99"), 15, "Electronics"
       );
       Product updatedProduct = new Product();
       updatedProduct.setId(1L);
       updatedProduct.setName("Updated Product");
       updatedProduct.setPrice(new BigDecimal("149.99"));

       when(productService.updateProduct(eq(1L), any(Product.class))).thenReturn(updatedProduct);

       mockMvc.perform(put("/api/products/1")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(updatedProductDTO))
               .with(csrf()))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(1))
               .andExpect(jsonPath("$.name").value("Updated Product"));

       verify(productService, times(1)).updateProduct(eq(1L), any(Product.class));
   }

    @Test
    void givenNoAuthentication_whenAccessingEndpoint_thenUnauthorizedIsReturned() throws Exception {
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isUnauthorized());

        verify(productService, never()).findProductById(anyLong());
    }
}
