package com.ecommerce.service;

import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.exception.EcommerceAPIException;
import com.ecommerce.entity.*;
import com.ecommerce.repository.OrderItemRepository;
import com.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private ProductService productService;

    @Transactional
    public Order createOrder(User user, Order orderRequest) {
        // Get user's cart
        Cart cart = cartService.getCartByUser(user);
        
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order with empty cart");
        }
        
        // Create new order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(Order.OrderStatus.PENDING);
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setBillingAddress(orderRequest.getBillingAddress());
        order.setPaymentMethod(orderRequest.getPaymentMethod());
        order.setCreatedAt(LocalDateTime.now());
        
        java.math.BigDecimal totalPrice = java.math.BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        
        // Convert cart items to order items
        for (CartItem cartItem : cart.getItems()) {
            // Update product stock
            Product updatedProduct = productService.updateStock(cartItem.getProduct().getId(), cartItem.getQuantity());
            
            // Get the product from cart item
            Product product = cartItem.getProduct();
            // Update the stock quantity to match what was returned from updateStock
            product.setStockQuantity(updatedProduct.getStockQuantity());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice().multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())));
                
            totalPrice = totalPrice.add(orderItem.getPrice());
            orderItems.add(orderItem);
        }
        
        order.setItems(orderItems);
        order.setTotalPrice(totalPrice);
        
        // Save order and items
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);
        
        // Clear the cart after order is placed
        cartService.clearCart(user);
        
        return savedOrder;
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }
    
    public Order getOrderByIdAndUser(Long id, User user) {
        return orderRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }
    
    public Page<Order> getOrdersByUser(User user, Pageable pageable) {
        return orderRepository.findByUser(user, pageable);
    }
    
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = getOrderById(id);
        
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(orderStatus);
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }
    
    @Transactional
    public Order updateOrderStatusForUser(Long id, User user, String status) {
        // First check if the order belongs to the user
        Order order = getOrderByIdAndUser(id, user);
        
        try {
            // Validate the status
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            
            // Regular users can only cancel their orders if they're still in PENDING or PROCESSING state
            if (orderStatus == Order.OrderStatus.CANCELLED) {
                if (order.getStatus() == Order.OrderStatus.PENDING || 
                    order.getStatus() == Order.OrderStatus.PROCESSING) {
                    order.setStatus(orderStatus);
                } else {
                    throw new EcommerceAPIException(HttpStatus.BAD_REQUEST, 
                        "Cannot cancel order that is already " + order.getStatus());
                }
            } else {
                // Regular users cannot change order status to anything else
                throw new EcommerceAPIException(HttpStatus.FORBIDDEN, 
                    "Users can only cancel orders, not change to other statuses");
            }
            
            return orderRepository.save(order);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }
    
    @Transactional
    public Order processPayment(Long orderId, String paymentMethod) {
        Order order = getOrderById(orderId);
        
        // In a real application, you would integrate with a payment gateway here
        // For now, we'll just update the order status and payment method
        
        order.setPaymentMethod(paymentMethod);
        order.setStatus(Order.OrderStatus.PROCESSING);
        return orderRepository.save(order);
    }
    
    /**
     * Get a list of all valid order statuses
     * @return List of order status strings
     */
    public List<String> getAllOrderStatuses() {
        return Arrays.stream(Order.OrderStatus.values())
            .map(Enum::name)
            .toList();
    }
    
    /**
     * Get the total number of orders
     * @return The count of all orders
     */
    public long getOrderCount() {
        return orderRepository.count();
    }
    
    /**
     * Get the most recent orders
     * @param limit The maximum number of orders to return
     * @return A list of the most recent orders
     */
    public List<Order> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return orderRepository.findAll(pageable).getContent();
    }
    
    /**
     * Calculate the total revenue from all completed orders
     * @return The total revenue
     */
    public double calculateTotalRevenue() {
        List<Order> completedOrders = orderRepository.findByStatus(Order.OrderStatus.DELIVERED);
        return completedOrders.stream()
            .mapToDouble(order -> order.getTotalPrice().doubleValue())
            .sum();
    }
    
    /**
     * Get monthly revenue for the last n months
     * @param months The number of months to include
     * @return A map of month names to revenue amounts
     */
    public Map<String, Double> getMonthlyRevenue(int months) {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
        List<Order> orders = orderRepository.findByStatusAndCreatedAtAfter(
            Order.OrderStatus.DELIVERED, startDate);
        
        Map<String, Double> monthlyRevenue = new HashMap<>();
        
        // Initialize all months with zero revenue
        for (int i = 0; i < months; i++) {
            String monthName = LocalDateTime.now().minusMonths(i).getMonth().toString();
            monthlyRevenue.put(monthName, 0.0);
        }
        
        // Calculate revenue for each month
        for (Order order : orders) {
            String monthName = order.getCreatedAt().getMonth().toString();
            double currentRevenue = monthlyRevenue.getOrDefault(monthName, 0.0);
            monthlyRevenue.put(monthName, currentRevenue + order.getTotalPrice().doubleValue());
        }
        
        return monthlyRevenue;
    }
    
    /**
     * Get orders by status
     * @param status The order status to filter by
     * @param pageable Pagination information
     * @return A page of orders with the specified status
     */
    public Page<Order> getOrdersByStatus(String status, Pageable pageable) {
        try {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            return orderRepository.findByStatus(orderStatus, pageable);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid order status: " + status);
        }
    }
    
    /**
     * Generate a sales report for a specific time period
     * @param startDate The start date (optional)
     * @param endDate The end date (optional)
     * @param groupBy How to group the data (daily, weekly, monthly)
     * @return A map containing the sales report data
     */
    public Map<String, Object> generateSalesReport(String startDate, String endDate, String groupBy) {
        LocalDateTime start = startDate != null ? 
            LocalDateTime.parse(startDate + "T00:00:00") : 
            LocalDateTime.now().minusMonths(1);
            
        LocalDateTime end = endDate != null ? 
            LocalDateTime.parse(endDate + "T23:59:59") : 
            LocalDateTime.now();
            
        List<Order> orders = orderRepository.findByCreatedAtBetween(start, end);
        
        Map<String, Object> report = new HashMap<>();
        report.put("startDate", start.toString());
        report.put("endDate", end.toString());
        report.put("totalOrders", orders.size());
        
        double totalRevenue = orders.stream()
            .mapToDouble(order -> order.getTotalPrice().doubleValue())
            .sum();
        report.put("totalRevenue", totalRevenue);
        
        // Group data based on the specified grouping
        Map<String, Double> groupedData = new HashMap<>();
        
        for (Order order : orders) {
            String key;
            switch (groupBy.toLowerCase()) {
                case "daily":
                    key = order.getCreatedAt().toLocalDate().toString();
                    break;
                case "weekly":
                    // Get week of year
                    key = "Week " + order.getCreatedAt().get(java.time.temporal.WeekFields.of(java.util.Locale.getDefault()).weekOfYear());
                    break;
                case "monthly":
                default:
                    key = order.getCreatedAt().getMonth().toString();
                    break;
            }
            
            double currentRevenue = groupedData.getOrDefault(key, 0.0);
            groupedData.put(key, currentRevenue + order.getTotalPrice().doubleValue());
        }
        
        report.put("groupedData", groupedData);
        
        return report;
    }
    
    /**
     * Generate a product performance report
     * @return A list of maps containing product performance data
     */
    public List<Map<String, Object>> generateProductPerformanceReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        
        // Get all completed orders
        List<Order> completedOrders = orderRepository.findByStatus(Order.OrderStatus.DELIVERED);
        
        // Create a map to track product sales
        Map<Long, Map<String, Object>> productSales = new HashMap<>();
        
        // Process all order items
        for (Order order : completedOrders) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                Long productId = product.getId();
                
                if (!productSales.containsKey(productId)) {
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("productId", productId);
                    productData.put("productName", product.getName());
                    productData.put("totalQuantitySold", 0);
                    productData.put("totalRevenue", 0.0);
                    productSales.put(productId, productData);
                }
                
                Map<String, Object> productData = productSales.get(productId);
                int currentQuantity = (int) productData.get("totalQuantitySold");
                double currentRevenue = (double) productData.get("totalRevenue");
                
                productData.put("totalQuantitySold", currentQuantity + item.getQuantity());
                productData.put("totalRevenue", currentRevenue + item.getPrice().doubleValue());
            }
        }
        
        // Convert the map to a list
        report.addAll(productSales.values());
        
        // Sort by total revenue (descending)
        report.sort((a, b) -> Double.compare((double) b.get("totalRevenue"), (double) a.get("totalRevenue")));
        
        return report;
    }
    
    /**
     * Generate a user activity report
     * @return A list of maps containing user activity data
     */
    public List<Map<String, Object>> generateUserActivityReport() {
        List<Map<String, Object>> report = new ArrayList<>();
        
        // Get all users with their orders
        List<User> users = orderRepository.findDistinctUsers();
        
        for (User user : users) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("userId", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            
            List<Order> userOrders = orderRepository.findByUser(user);
            userData.put("totalOrders", userOrders.size());
            
            double totalSpent = userOrders.stream()
                .mapToDouble(order -> order.getTotalPrice().doubleValue())
                .sum();
            userData.put("totalSpent", totalSpent);
            
            if (!userOrders.isEmpty()) {
                LocalDateTime lastOrderDate = userOrders.stream()
                    .map(Order::getCreatedAt)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);
                userData.put("lastOrderDate", lastOrderDate != null ? lastOrderDate.toString() : null);
            } else {
                userData.put("lastOrderDate", null);
            }
            
            report.add(userData);
        }
        
        // Sort by total spent (descending)
        report.sort((a, b) -> Double.compare((double) b.get("totalSpent"), (double) a.get("totalSpent")));
        
        return report;
    }
}
