package com.ecommerce.service;

import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.entity.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
    }
    
    public Page<Product> searchProducts(String query, Pageable pageable) {
        return productRepository.searchProducts(query, pageable);
    }
    
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }
    
    public Page<Product> getProductsByBrand(String brand, Pageable pageable) {
        return productRepository.findByBrand(brand, pageable);
    }
    
    public Page<String> getAllCategories(Pageable pageable) {
        return productRepository.findAllCategories(pageable);
    }
    
    public Page<String> getAllBrands(Pageable pageable) {
        return productRepository.findAllBrands(pageable);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product entity = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        entity.setName(productDetails.getName());
        entity.setDescription(productDetails.getDescription());
        entity.setPrice(productDetails.getPrice());
        entity.setCategory(productDetails.getCategory());
        entity.setBrand(productDetails.getBrand());
        entity.setImageUrl(productDetails.getImageUrl());
        entity.setStockQuantity(productDetails.getStockQuantity());
        
        return productRepository.save(entity);
    }
    
    @Transactional
    public Product updateStock(Long id, Integer quantity) {
        Product entity = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        if (entity.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available for product: " + entity.getName());
        }
        
        entity.setStockQuantity(entity.getStockQuantity() - quantity);
        return productRepository.save(entity);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product entity = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        productRepository.delete(entity);
    }
    
    /**
     * Get the total number of products
     * @return The count of all products
     */
    public long getProductCount() {
        return productRepository.count();
    }
}

