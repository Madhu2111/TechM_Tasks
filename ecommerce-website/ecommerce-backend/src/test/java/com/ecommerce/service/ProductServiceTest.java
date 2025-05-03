package com.ecommerce.service;

import com.ecommerce.entity.Product;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    public ProductServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllProducts() {
        // Create entity products (used by repository)
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("Phone");
        p1.setDescription("Smartphone");
        p1.setPrice(BigDecimal.valueOf(12000.0));
        p1.setStockQuantity(10);
        p1.setCategory("Electronics");
        p1.setBrand("Samsung");
        
        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("Laptop");
        p2.setDescription("Gaming Laptop");
        p2.setPrice(BigDecimal.valueOf(50000.0));
        p2.setStockQuantity(5);
        p2.setCategory("Electronics");
        p2.setBrand("Dell");
        
        List<Product> entityProducts = Arrays.asList(p1, p2);
        
        // Create model products (returned by service)
        com.ecommerce.entity.Product modelP1 = com.ecommerce.entity.Product.builder()
            .id(1L)
            .name("Phone")
            .description("Smartphone")
            .price(BigDecimal.valueOf(12000.0))
            .stockQuantity(10)
            .category("Electronics")
            .brand("Samsung")
            .build();
            
        com.ecommerce.entity.Product modelP2 = com.ecommerce.entity.Product.builder()
            .id(2L)
            .name("Laptop")
            .description("Gaming Laptop")
            .price(BigDecimal.valueOf(50000.0))
            .stockQuantity(5)
            .category("Electronics")
            .brand("Dell")
            .build();
            
        List<com.ecommerce.entity.Product> modelProducts = Arrays.asList(modelP1, modelP2);

        // Mock repository and mapper
        when(productRepository.findAll()).thenReturn(entityProducts);
        when(productMapper.entitiesToModels(entityProducts)).thenReturn(modelProducts);

        // Test service
        List<com.ecommerce.entity.Product> products = productService.getAllProducts();
        assertEquals(2, products.size());
    }

    @Test
    public void testGetProductById() {
        // Create entity product (used by repository)
        Product p = new Product();
        p.setId(1L);
        p.setName("TV");
        p.setDescription("Smart TV");
        p.setPrice(BigDecimal.valueOf(30000.0));
        p.setStockQuantity(4);
        p.setCategory("Electronics");
        p.setBrand("Sony");
        
        // Create model product (returned by service)
        com.ecommerce.entity.Product modelP = com.ecommerce.entity.Product.builder()
            .id(1L)
            .name("TV")
            .description("Smart TV")
            .price(BigDecimal.valueOf(30000.0))
            .stockQuantity(4)
            .category("Electronics")
            .brand("Sony")
            .build();
            
        // Mock repository and mapper
        when(productRepository.findById(1L)).thenReturn(Optional.of(p));
        when(productMapper.entityToModel(p)).thenReturn(modelP);

        // Test service
        com.ecommerce.entity.Product found = productService.getProductById(1L);
        assertNotNull(found);
        assertEquals("TV", found.getName());
    }
}
