package com.ecommerce.service;

import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.CartItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.User;
import com.ecommerce.repository.CartItemRepository;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public Cart getCartByUser(User user) {
        System.out.println("Getting cart for user: " + user.getUsername());
        return cartRepository.findByUser(user)
            .orElseGet(() -> {
                System.out.println("No cart found, creating new cart for user: " + user.getUsername());
                Cart newCart = new Cart();
                newCart.setUser(user);
                newCart.setTotalPrice(java.math.BigDecimal.ZERO);
                return cartRepository.save(newCart);
            });
    }

    @Transactional
    public Cart addProductToCart(User user, Long productId, int quantity) {
        System.out.println("Adding product ID: " + productId + " with quantity: " + quantity + " to cart for user: " + user.getUsername());
        
        if (quantity <= 0) {
            System.out.println("Invalid quantity: " + quantity);
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        
        Cart cart = getCartByUser(user);
        System.out.println("Got cart with ID: " + cart.getId());
        
        try {
            System.out.println("Finding product with ID: " + productId);
            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
            
            System.out.println("Found product: " + product.getName() + " with stock: " + product.getStockQuantity());
                
            // Check if product is in stock
            if (product.getStockQuantity() < quantity) {
                System.out.println("Not enough stock. Requested: " + quantity + ", Available: " + product.getStockQuantity());
                throw new IllegalArgumentException("Not enough stock available for product: " + product.getName());
            }
    
            // Check if product already exists in cart
            System.out.println("Checking if product already exists in cart. Items count: " + cart.getItems().size());
            Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    
            if (existingItem.isPresent()) {
                // Update quantity if product already in cart
                CartItem item = existingItem.get();
                System.out.println("Product already in cart. Current quantity: " + item.getQuantity() + ", Adding: " + quantity);
                item.setQuantity(item.getQuantity() + quantity);
                cartItemRepository.save(item);
                System.out.println("Updated cart item quantity to: " + item.getQuantity());
            } else {
                // Add new item to cart
                System.out.println("Adding new item to cart");
                CartItem cartItem = new CartItem();
                cartItem.setCart(cart);
                cartItem.setProduct(product);
                cartItem.setQuantity(quantity);
                cartItemRepository.save(cartItem);
                cart.getItems().add(cartItem);
                System.out.println("Added new item to cart");
            }
            
            // Recalculate total price
            System.out.println("Recalculating total price");
            updateCartTotalPrice(cart);
            
            System.out.println("Saving cart");
            return cartRepository.save(cart);
        } catch (Exception e) {
            System.out.println("Error in addProductToCart: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    @Transactional
    public Cart updateCartItemQuantity(User user, Long productId, int quantity) {
        Cart cart = getCartByUser(user);
        
        if (quantity <= 0) {
            return removeProductFromCart(user, productId);
        }
        
        CartItem cartItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));
            
        Product product = cartItem.getProduct();
        
        // Check if product is in stock
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock available for product: " + product.getName());
        }
        
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
        
        // Recalculate total price
        updateCartTotalPrice(cart);
        
        return cartRepository.save(cart);
    }
    
    @Transactional
    public Cart removeProductFromCart(User user, Long productId) {
        Cart cart = getCartByUser(user);
        
        CartItem cartItem = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("CartItem", "productId", productId));
            
        cart.getItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        
        // Recalculate total price
        updateCartTotalPrice(cart);
        
        return cartRepository.save(cart);
    }
    
    @Transactional
    public Cart clearCart(User user) {
        Cart cart = getCartByUser(user);
        
        List<CartItem> items = cart.getItems();
        cart.getItems().clear();
        cartItemRepository.deleteAll(items);
        
        cart.setTotalPrice(java.math.BigDecimal.ZERO);
        
        return cartRepository.save(cart);
    }
    
    @Transactional
    public Cart syncCart(User user, Cart clientCart) {
        // Clear existing cart
        clearCart(user);
        
        // Add all items from client cart
        if (clientCart.getItems() != null && !clientCart.getItems().isEmpty()) {
            for (CartItem item : clientCart.getItems()) {
                try {
                    addProductToCart(user, item.getProduct().getId(), item.getQuantity());
                } catch (Exception e) {
                    // Skip invalid items
                    System.out.println("Error adding product to cart: " + e.getMessage());
                }
            }
        }
        
        // Return the updated server cart
        return getCartByUser(user);
    }
    
    private void updateCartTotalPrice(Cart cart) {
        java.math.BigDecimal totalPrice = cart.getItems().stream()
            .map(item -> item.getProduct().getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
            
        cart.setTotalPrice(totalPrice);
    }
}

