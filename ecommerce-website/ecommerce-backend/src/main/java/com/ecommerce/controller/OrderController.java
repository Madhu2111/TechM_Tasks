package com.ecommerce.controller;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin
@Tag(name = "Order Controller", description = "APIs for managing orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Operation(
        summary = "Create a new order",
        description = "Creates a new order for the authenticated user"
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> createOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody Order orderRequest) {
        
        User user = userService.getUserByUsername(userDetails.getUsername());
        Order order = orderService.createOrder(user, orderRequest);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }
    
    @Operation(
        summary = "Get order by ID",
        description = "Retrieves an order by its ID for the authenticated user"
    )
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> getOrderById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        
        User user = userService.getUserByUsername(userDetails.getUsername());
        Order order = orderService.getOrderByIdAndUser(id, user);
        return ResponseEntity.ok(order);
    }
    
    @Operation(
        summary = "Get current user's orders",
        description = "Retrieves all orders for the authenticated user"
    )
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Order>> getUserOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = userService.getUserByUsername(userDetails.getUsername());
        List<Order> orders = orderService.getOrdersByUser(user);
        
        return ResponseEntity.ok(orders);
    }
    
    @Operation(
        summary = "Get current user's orders with pagination",
        description = "Retrieves all orders for the authenticated user with pagination"
    )
    @GetMapping("/user/paged")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Order>> getUserOrdersPaged(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        
        User user = userService.getUserByUsername(userDetails.getUsername());
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
                Sort.Direction.DESC : Sort.Direction.ASC;
                
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Order> orders = orderService.getOrdersByUser(user, pageable);
        
        return ResponseEntity.ok(orders);
    }
    
    @Operation(
        summary = "Get all orders (Admin)",
        description = "Retrieves all orders. Requires ADMIN role."
    )
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
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
        summary = "Update order status",
        description = "Updates the status of an order. Users can only update their own orders, admins can update any order."
    )
    @PatchMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> updateOrderStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, String> statusUpdate) {
        
        String status = statusUpdate.get("status");
        if (status == null || status.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = userService.getUserByUsername(userDetails.getUsername());
        Order order;
        
        // If user is admin, they can update any order
        if (user.getRoles().stream().anyMatch(role -> role.getName() == Role.ERole.ROLE_ADMIN)) {
            order = orderService.updateOrderStatus(id, status);
        } else {
            // Regular users can only update their own orders
            order = orderService.updateOrderStatusForUser(id, user, status);
        }
        
        return ResponseEntity.ok(order);
    }
    
    @Operation(
        summary = "Process payment",
        description = "Processes payment for an order"
    )
    @PostMapping("/{id}/payment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Order> processPayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody Map<String, String> paymentDetails) {
        
        User user = userService.getUserByUsername(userDetails.getUsername());
        
        // Verify that the order belongs to the user
        Order order = orderService.getOrderByIdAndUser(id, user);
        
        // Process the payment and return the updated order
        Order updatedOrder = orderService.processPayment(order.getId(), paymentDetails.get("paymentMethod"));
        return ResponseEntity.ok(updatedOrder);
    }
    
    @Operation(
        summary = "Get all order statuses",
        description = "Retrieves all possible order statuses"
    )
    @GetMapping("/statuses")
    @CrossOrigin
    public ResponseEntity<List<String>> getAllOrderStatuses() {
        return ResponseEntity.ok(orderService.getAllOrderStatuses());
    }
}
