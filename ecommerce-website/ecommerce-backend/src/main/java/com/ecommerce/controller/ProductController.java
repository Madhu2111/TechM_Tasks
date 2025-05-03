package com.ecommerce.controller;

import com.ecommerce.entity.Product;
import com.ecommerce.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin
@Tag(name = "Product Controller", description = "APIs for managing products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Operation(
        summary = "Create a new product",
        description = "Creates a new product with the provided details. Requires ADMIN role."
    )
    @ApiResponse(
        responseCode = "201",
        description = "Product created successfully"
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.CREATED);
    }

    @Operation(
        summary = "Get all products",
        description = "Retrieves all products with pagination and sorting options"
    )
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Product> products = productService.getAllProducts(pageable);
        
        return ResponseEntity.ok(products);
    }

    @Operation(
        summary = "Get product by ID",
        description = "Retrieves a product by its ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @Operation(
        summary = "Search products",
        description = "Searches products by name, description, brand, or category"
    )
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.searchProducts(query, pageable);
        
        return ResponseEntity.ok(products);
    }
    
    @Operation(
        summary = "Get products by category",
        description = "Retrieves products filtered by category"
    )
    @GetMapping("/category")
    public ResponseEntity<Page<Product>> getProductsByCategory(
            @RequestParam String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByCategory(category, pageable);
        
        return ResponseEntity.ok(products);
    }
    
    @Operation(
        summary = "Get products by brand",
        description = "Retrieves products filtered by brand"
    )
    @GetMapping("/brand")
    public ResponseEntity<Page<Product>> getProductsByBrand(
            @RequestParam String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.getProductsByBrand(brand, pageable);
        
        return ResponseEntity.ok(products);
    }
    
    @Operation(
        summary = "Get all categories",
        description = "Retrieves all unique product categories"
    )
    @GetMapping("/categories")
    public ResponseEntity<Page<String>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<String> categories = productService.getAllCategories(pageable);
        
        return ResponseEntity.ok(categories);
    }
    
    @Operation(
        summary = "Get all brands",
        description = "Retrieves all unique product brands"
    )
    @GetMapping("/brands")
    public ResponseEntity<Page<String>> getAllBrands(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<String> brands = productService.getAllBrands(pageable);
        
        return ResponseEntity.ok(brands);
    }
    
    @Operation(
        summary = "Update a product",
        description = "Updates an existing product. Requires ADMIN role."
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id, 
            @Valid @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @Operation(
        summary = "Delete a product",
        description = "Deletes a product by its ID. Requires ADMIN role."
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
