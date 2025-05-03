package com.ecommerce.controller;

import com.ecommerce.entity.User;
import com.ecommerce.payload.JwtAuthResponse;
import com.ecommerce.payload.LoginRequest;
import com.ecommerce.payload.SignUpRequest;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.security.JwtTokenProvider;
import com.ecommerce.repository.RoleRepository;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.Role.ERole;
import java.util.HashSet;
import java.util.Set;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private RoleRepository roleRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        
        return ResponseEntity.ok(new JwtAuthResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        logger.debug("Received registration request for username: {}", signUpRequest.getUsername());
        
        try {
            // Check if username is already taken
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                logger.debug("Username is already taken: {}", signUpRequest.getUsername());
                return new ResponseEntity<>(Map.of("message", "Username is already taken!"), HttpStatus.BAD_REQUEST);
            }
    
            // Check if email is already in use
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                logger.debug("Email is already in use: {}", signUpRequest.getEmail());
                return new ResponseEntity<>(Map.of("message", "Email is already in use!"), HttpStatus.BAD_REQUEST);
            }
    
            // Create new user with builder pattern
            String role = signUpRequest.getRole() != null ? signUpRequest.getRole().toUpperCase() : "CUSTOMER";
            logger.debug("Creating new user with role: {}", role);
            
            // Map the role to the corresponding ERole
            ERole eRole;
            switch (role) {
                case "ADMIN":
                    eRole = ERole.ROLE_ADMIN;
                    break;
                case "CUSTOMER":
                case "USER":
                    eRole = ERole.ROLE_USER;
                    break;
                default:
                    logger.error("Invalid role specified: {}", role);
                    return new ResponseEntity<>(Map.of("message", "Invalid role specified!"), HttpStatus.BAD_REQUEST);
            }
            
            // Find the role entity
            Role userRole = roleRepository.findByName(eRole)
                .orElseThrow(() -> new RuntimeException("Error: Role not found: " + eRole));
            
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            
            User user = User.builder()
                    .username(signUpRequest.getUsername())
                    .email(signUpRequest.getEmail())
                    .password(passwordEncoder.encode(signUpRequest.getPassword()))
                    .roles(roles)
                    .build();
    
            userRepository.save(user);
            logger.info("User registered successfully: {}", signUpRequest.getUsername());
    
            // Return a JSON response instead of a plain text response
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (Exception e) {
            logger.error("Error registering user: {}", e.getMessage(), e);
            return new ResponseEntity<>(
                Map.of("message", "Error registering user: " + e.getMessage()), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}