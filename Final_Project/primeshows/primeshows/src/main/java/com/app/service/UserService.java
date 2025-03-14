package com.app.service;

import com.app.dto.UserDTO;
import com.app.dto.AuthResponseDTO;
import com.app.exception.ResourceNotFoundException;
import com.app.exception.UserAlreadyExistsException;
import com.app.model.Role;
import com.app.model.User;
import com.app.repository.UserRepository;
import com.app.security.JwtUtil;
import com.app.security.CustomUserDetails;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil; //  Inject JWT utility

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ✅ **Register New User**
    public User registerUser(UserDTO userDto) {
        if (userRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User already registered with this email!");
        }

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        
        //  Encrypt password before storing
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); 
        
        // Assinging role at time of register 
        if(userDto.getRole()!=null && (userDto.getRole()==Role.ADMIN || userDto.getRole()==Role.SHOW_ORGANIZER)) {
        	user.setRole(userDto.getRole());
        }
        else {
        	user.setRole(Role.USER);
        }
        return userRepository.save(user);
    }

    //  Login User (Verify Password & Generate JWT)
    public AuthResponseDTO loginUser(UserDTO loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + loginDto.getEmail()));

        // Compare entered password with stored hashed password
        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Convert User to UserDetails for JWT generation
        UserDetails userDetails = new CustomUserDetails(user);
        
        //  Generate JWT token after successful authentication
        String token = jwtUtil.generateToken(userDetails);
        

        return new AuthResponseDTO(token);
        
        
        
    }

    // Get User by Email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null); // ✅ Return user if found
    }

    // Get User by ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
    }
}

