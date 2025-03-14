package com.app.controller;

import com.app.dto.AuthResponseDTO;
import com.app.dto.UserDTO;
import com.app.model.User;
import com.app.model.Role; 
import com.app.security.JwtUtil;
import com.app.service.UserService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    public UserController(AuthenticationManager authenticationManager, JwtUtil jwtUtil,
                          UserDetailsService userDetailsService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserDTO userDto) {
        User registeredUser = userService.registerUser(userDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody UserDTO userDto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getEmail());
            String token = jwtUtil.generateToken(userDetails);

            // ✅ Fetch User from Database
            User user = userService.findByEmail(userDto.getEmail());
            if (user == null) {
                return ResponseEntity.status(404).body(null);
            }

            // ✅ Prevent Null Role
            String role = (user.getRole() != null) ? user.getRole().name() : "USER";

            return ResponseEntity.ok(new AuthResponseDTO(token, role));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(null);
        }
    }

}

