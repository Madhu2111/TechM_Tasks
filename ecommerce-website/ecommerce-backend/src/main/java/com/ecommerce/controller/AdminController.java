package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Controller", description = "APIs for admin dashboard and management")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Operation(
        summary = "Get dashboard statistics",
        description = "Retrieves key statistics for the admin dashboard"
    )
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get total users count
        long totalUsers = userService.getUserCount();
        stats.put("totalUsers", totalUsers);
        
        // Get total orders count
        long totalOrders = orderService.getOrderCount();
        stats.put("totalOrders", totalOrders);
        
        // Get total products count
        long totalProducts = productService.getProductCount();
        stats.put("totalProducts", totalProducts);
        
        // Get recent orders
        List<Order> recentOrders = orderService.getRecentOrders(5);
        stats.put("recentOrders", recentOrders);
        
        // Get revenue statistics
        double totalRevenue = orderService.calculateTotalRevenue();
        stats.put("totalRevenue", totalRevenue);
        
        // Get monthly revenue for the last 6 months
        Map<String, Double> monthlyRevenue = orderService.getMonthlyRevenue(6);
        stats.put("monthlyRevenue", monthlyRevenue);
        
        return ResponseEntity.ok(stats);
    }

    // User Management
    
    @Operation(
        summary = "Get all users",
        description = "Retrieves all users with pagination and sorting options"
    )
    @GetMapping("/users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<User> users = userService.getAllUsers(pageable);
        
        return ResponseEntity.ok(users);
    }
    
    @Operation(
        summary = "Get user by ID",
        description = "Retrieves a user by their ID"
    )
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @Operation(
        summary = "Update user",
        description = "Updates a user's information"
    )
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody User userData) {
        User updatedUser = userService.updateUser(id, userData);
        return ResponseEntity.ok(updatedUser);
    }
    
    @Operation(
        summary = "Delete user",
        description = "Deletes a user by their ID"
    )
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // Order Management
    
    @Operation(
        summary = "Get all orders",
        description = "Retrieves all orders with pagination and sorting options"
    )
    @GetMapping("/orders")
    public ResponseEntity<Page<Order>> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Order> orders = orderService.getAllOrders(pageable);
        
        return ResponseEntity.ok(orders);
    }
    
    @Operation(
        summary = "Get order by ID",
        description = "Retrieves an order by its ID"
    )
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
    
    @Operation(
        summary = "Update order status",
        description = "Updates the status of an order"
    )
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        
        String status = statusUpdate.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        Order order = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(order);
    }
    
    @Operation(
        summary = "Get orders by status",
        description = "Retrieves orders filtered by status"
    )
    @GetMapping("/orders/status/{status}")
    public ResponseEntity<Page<Order>> getOrdersByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderService.getOrdersByStatus(status, pageable);
        
        return ResponseEntity.ok(orders);
    }
    
    // Analytics and Reports
    
    @Operation(
        summary = "Get sales report",
        description = "Retrieves sales data for a specific time period"
    )
    @GetMapping("/reports/sales")
    public ResponseEntity<Map<String, Object>> getSalesReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "daily") String groupBy) {
        
        Map<String, Object> salesReport = orderService.generateSalesReport(startDate, endDate, groupBy);
        return ResponseEntity.ok(salesReport);
    }
    
    @Operation(
        summary = "Get product performance report",
        description = "Retrieves performance data for products"
    )
    @GetMapping("/reports/products")
    public ResponseEntity<List<Map<String, Object>>> getProductPerformanceReport() {
        List<Map<String, Object>> productReport = orderService.generateProductPerformanceReport();
        return ResponseEntity.ok(productReport);
    }
    
    @Operation(
        summary = "Get user activity report",
        description = "Retrieves user activity data"
    )
    @GetMapping("/reports/users")
    public ResponseEntity<List<Map<String, Object>>> getUserActivityReport() {
        List<Map<String, Object>> userReport = orderService.generateUserActivityReport();
        return ResponseEntity.ok(userReport);
    }
}