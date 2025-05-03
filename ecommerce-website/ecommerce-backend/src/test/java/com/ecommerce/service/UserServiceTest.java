package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveUser() {
        User user = new User();
        user.setUsername("madhu");
        user.setPassword("password");
        user.setEmail("madhu@example.com");

        when(userRepository.save(any())).thenReturn(user);

        User savedUser = userService.saveUser(user);
        assertNotNull(savedUser);
        assertEquals("madhu", savedUser.getUsername());
    }

    @Test
    public void testGetUserByUsername() {
        User user = new User();
        user.setUsername("madhu");

        when(userRepository.findByUsername("madhu")).thenReturn(Optional.of(user));

        User foundUser = userService.getUserByUsername("madhu");
        assertNotNull(foundUser);
        assertEquals("madhu", foundUser.getUsername());
    }
}
