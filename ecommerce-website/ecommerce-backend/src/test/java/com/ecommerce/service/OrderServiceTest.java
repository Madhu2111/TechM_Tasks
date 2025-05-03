package com.ecommerce.service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.User;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    public OrderServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetOrdersByUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("madhu");

        Order order1 = new Order();
        order1.setId(101L);
        order1.setUser(user);

        Order order2 = new Order();
        order2.setId(102L);
        order2.setUser(user);

        when(userRepository.findByUsername("madhu")).thenReturn(Optional.of(user));
        when(orderRepository.findByUser(user)).thenReturn(Arrays.asList(order1, order2));

        // Pass the user object directly to the service method
        List<Order> orders = orderService.getOrdersByUser(user);

        assertEquals(2, orders.size());
        assertEquals(101L, orders.get(0).getId());
    }
}
