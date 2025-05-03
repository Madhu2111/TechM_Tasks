package com.ecommerce.controller;

import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.CartService;
import com.ecommerce.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin
@Tag(name = "Cart Controller", description = "APIs for managing shopping cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductRepository productRepository;

    @Operation(
        summary = "Get current user's cart",
        description = "Retrieves the cart for the authenticated user"
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> getCurrentUserCart(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            String username = userDetails.getUsername();
            // Check if user exists before trying to get it
            if (!userService.findByUsername(username).isPresent()) {
                return ResponseEntity.status(404).body(null);
            }
            
            User user = userService.getUserByUsername(username);
            Cart cart = cartService.getCartByUser(user);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            System.out.println("Error in getCurrentUserCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @Operation(
        summary = "Get current user's cart (alternative endpoint)",
        description = "Retrieves the cart for the authenticated user"
    )
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> getUserCart(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            String username = userDetails.getUsername();
            // Check if user exists before trying to get it
            if (!userService.findByUsername(username).isPresent()) {
                return ResponseEntity.status(404).body(null);
            }
            
            User user = userService.getUserByUsername(username);
            Cart cart = cartService.getCartByUser(user);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            System.out.println("Error in getUserCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }

    @Operation(
        summary = "Add product to cart",
        description = "Adds a product to the authenticated user's cart"
    )
    @PostMapping("/items")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            String username = userDetails.getUsername();
            // Check if user exists before trying to get it
            if (!userService.findByUsername(username).isPresent()) {
                return ResponseEntity.status(404).body(null);
            }
            
            User user = userService.getUserByUsername(username);
            Cart cart = cartService.addProductToCart(user, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            System.out.println("Error in addToCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @Operation(
        summary = "Update cart item quantity",
        description = "Updates the quantity of a product in the authenticated user's cart"
    )
    @PutMapping("/items/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> updateCartItemQuantity(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            String username = userDetails.getUsername();
            // Check if user exists before trying to get it
            if (!userService.findByUsername(username).isPresent()) {
                return ResponseEntity.status(404).body(null);
            }
            
            User user = userService.getUserByUsername(username);
            Cart cart = cartService.updateCartItemQuantity(user, productId, quantity);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            System.out.println("Error in updateCartItemQuantity: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @Operation(
        summary = "Remove product from cart",
        description = "Removes a product from the authenticated user's cart"
    )
    @DeleteMapping("/items/{productId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long productId) {
        
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            String username = userDetails.getUsername();
            // Check if user exists before trying to get it
            if (!userService.findByUsername(username).isPresent()) {
                return ResponseEntity.status(404).body(null);
            }
            
            User user = userService.getUserByUsername(username);
            Cart cart = cartService.removeProductFromCart(user, productId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            System.out.println("Error in removeFromCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @Operation(
        summary = "Clear cart",
        description = "Removes all items from the authenticated user's cart"
    )
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        
        try {
            String username = userDetails.getUsername();
            // Check if user exists before trying to get it
            if (!userService.findByUsername(username).isPresent()) {
                return ResponseEntity.status(404).body(null);
            }
            
            User user = userService.getUserByUsername(username);
            Cart cart = cartService.clearCart(user);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            System.out.println("Error in clearCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @Operation(
        summary = "Sync cart",
        description = "Synchronizes the client-side cart with the server-side cart"
    )
    @PostMapping("/sync")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cart> syncCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> clientCartData) {
        
        System.out.println("Cart sync request received");
        
        if (userDetails == null) {
            System.out.println("UserDetails is null");
            return ResponseEntity.status(401).build();
        }
        
        try {
            String username = userDetails.getUsername();
            System.out.println("Getting user by username: " + username);
            
            // Check if user exists before trying to get it
            if (!userService.findByUsername(username).isPresent()) {
                System.out.println("User not found with username: " + username);
                return ResponseEntity.status(404).body(null);
            }
            
            User user = userService.getUserByUsername(username);
            System.out.println("User found: " + user.getUsername());
            
            // First clear the existing cart
            System.out.println("Clearing existing cart");
            cartService.clearCart(user);
            
            // Extract items from the client cart data
            System.out.println("Client cart data: " + clientCartData);
            
            if (clientCartData.containsKey("items") && clientCartData.get("items") instanceof List<?>) {
                List<?> rawList = (List<?>) clientCartData.get("items");
                List<Map<String, Object>> itemsList = new ArrayList<>();
                
                // Safely convert each item to Map<String, Object> with runtime type checking
                for (Object item : rawList) {
                    if (item instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> itemData = (Map<String, Object>) item;
                        itemsList.add(itemData);
                    } else {
                        System.out.println("Skipping non-map item in items list: " + item);
                    }
                }
                
                System.out.println("Items list size: " + itemsList.size());
                
                for (Map<String, Object> itemData : itemsList) {
                    System.out.println("Processing item: " + itemData);
                    
                    if (itemData.containsKey("product") && itemData.get("product") instanceof Map<?, ?>) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> productData = (Map<String, Object>) itemData.get("product");
                        System.out.println("Product data: " + productData);
                        
                        if (productData.containsKey("id") && itemData.containsKey("quantity")) {
                            Long productId = Long.valueOf(productData.get("id").toString());
                            Integer quantity = Integer.valueOf(itemData.get("quantity").toString());
                            
                            System.out.println("Adding product ID: " + productId + " with quantity: " + quantity);
                            
                            // Add product to cart
                            try {
                                // Check if product exists before adding to cart
                                Product product = productRepository.findById(productId).orElse(null);
                                if (product != null) {
                                    cartService.addProductToCart(user, productId, quantity);
                                } else {
                                    System.out.println("Product with ID " + productId + " not found in database");
                                }
                            } catch (Exception e) {
                                System.out.println("Error adding product to cart: " + e.getMessage());
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Missing id or quantity in item data");
                        }
                    } else {
                        System.out.println("Missing product data in item");
                    }
                }
            } else {
                System.out.println("No items found in client cart data");
            }
            
            // Return the updated cart
            System.out.println("Getting updated cart for user");
            Cart cart = cartService.getCartByUser(user);
            System.out.println("Returning cart with " + (cart.getItems() != null ? cart.getItems().size() : 0) + " items");
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            System.out.println("Error in syncCart: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
