package com.showvault.service.impl;

import com.showvault.model.UserPreferences;
import com.showvault.models.ERole;
import com.showvault.models.Role;
import com.showvault.models.User;
import com.showvault.repository.RoleRepository;
import com.showvault.repository.UserPreferencesRepository;
import com.showvault.repository.UserRepository;
import com.showvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserPreferencesRepository userPreferencesRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, 
                      UserPreferencesRepository userPreferencesRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userPreferencesRepository = userPreferencesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public User registerUser(User user, String roleName) {
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set user as active
        user.setActive(true);
        
        // If roles are not already set and roleName is provided, assign role
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            if (roleName != null) {
                ERole eRole;
                switch (roleName.toLowerCase()) {
                    case "admin":
                        eRole = ERole.ROLE_ADMIN;
                        break;
                    case "organizer":
                        eRole = ERole.ROLE_ORGANIZER;
                        break;
                    default:
                        eRole = ERole.ROLE_USER;
                }
                roleRepository.findByName(eRole).ifPresent(roles::add);
            } else {
                // Default to USER role if none specified
                roleRepository.findByName(ERole.ROLE_USER).ifPresent(roles::add);
            }
            user.setRoles(roles);
        }
        
        // Save the user
        User savedUser = userRepository.save(user);
        
        // Detach the user from the persistence context to avoid lazy loading issues
        return userRepository.findById(savedUser.getId()).orElse(savedUser);
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Verify current password
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                // Update with new password
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        
        return false;
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setActive(false);
            userRepository.save(user);
        });
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setActive(true);
            userRepository.save(user);
        });
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    @Override
    @Transactional
    public User saveProfilePicture(Long userId, byte[] profilePicture, String fileName) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // In a real implementation, this would save the file to a storage service
            // and store the URL in the user record. For now, we'll just store the filename.
            
            // Simulate a URL for the profile picture
            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
            String profilePictureUrl = "/uploads/profile-pictures/" + userId + "_" + System.currentTimeMillis() + fileExtension;
            
            // In a real implementation, we would add a profilePictureUrl field to the User model
            // user.setProfilePictureUrl(profilePictureUrl);
            
            return userRepository.save(user);
        }
        
        return null;
    }
    
    @Override
    public UserPreferences getUserPreferences(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<UserPreferences> preferencesOpt = userPreferencesRepository.findByUser(user);
            
            if (preferencesOpt.isPresent()) {
                return preferencesOpt.get();
            } else {
                // Create default preferences
                UserPreferences preferences = new UserPreferences();
                preferences.setUser(user);
                return userPreferencesRepository.save(preferences);
            }
        }
        
        return null;
    }
    
    @Override
    @Transactional
    public User updateUserPreferences(Long userId, UserPreferences preferences) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Optional<UserPreferences> existingPreferencesOpt = userPreferencesRepository.findByUser(user);
            
            UserPreferences userPreferences;
            if (existingPreferencesOpt.isPresent()) {
                userPreferences = existingPreferencesOpt.get();
                userPreferences.setEmailNotifications(preferences.isEmailNotifications());
                userPreferences.setSmsNotifications(preferences.isSmsNotifications());
                userPreferences.setLanguage(preferences.getLanguage());
                userPreferences.setCurrency(preferences.getCurrency());
                userPreferences.setFavoriteCategories(preferences.getFavoriteCategories());
            } else {
                userPreferences = preferences;
                userPreferences.setUser(user);
            }
            
            userPreferencesRepository.save(userPreferences);
            return user;
        }
        
        return null;
    }

    @Override
    public long countUsersByRole(ERole roleName) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName() == roleName))
                .count();
    }

    @Override
    public long countUsersByRole(String roleName) {
        // Convert string role name to ERole if needed
        try {
            ERole eRole = ERole.valueOf(roleName);
            return countUsersByRole(eRole);
        } catch (IllegalArgumentException e) {
            // If the string doesn't match exactly, try with ROLE_ prefix
            if (!roleName.startsWith("ROLE_")) {
                return userRepository.countUsersByRoleName("ROLE_" + roleName.toUpperCase());
            } else {
                return userRepository.countUsersByRoleName(roleName);
            }
        }
    }
    
    @Override
    public List<User> getUsersWithFilters(int offset, int limit, String role, String status, String search, String sortBy, String sortOrder) {
        // In a real implementation, this would use a repository method with filters
        // For now, we'll simulate filtering with a basic implementation
        
        List<User> allUsers = userRepository.findAll();
        List<User> filteredUsers = new ArrayList<>();
        
        for (User user : allUsers) {
            boolean roleMatch = role == null || user.getRoles().stream()
                    .anyMatch(r -> r.getName().name().equals(role) || 
                                  r.getName().name().equals("ROLE_" + role.toUpperCase()));
            
            boolean statusMatch = status == null || 
                                 (status.equalsIgnoreCase("active") && user.isActive()) ||
                                 (status.equalsIgnoreCase("inactive") && !user.isActive());
            
            boolean searchMatch = search == null || 
                                 user.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                                 user.getEmail().toLowerCase().contains(search.toLowerCase()) ||
                                 (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(search.toLowerCase())) ||
                                 (user.getLastName() != null && user.getLastName().toLowerCase().contains(search.toLowerCase()));
            
            if (roleMatch && statusMatch && searchMatch) {
                filteredUsers.add(user);
            }
        }
        
        // Apply pagination
        int end = Math.min(offset + limit, filteredUsers.size());
        if (offset >= filteredUsers.size()) {
            return new ArrayList<>();
        }
        
        return filteredUsers.subList(offset, end);
    }
    
    @Override
    public long countUsersWithFilters(String role, String status, String search) {
        // In a real implementation, this would use a repository method with filters
        // For now, we'll simulate counting with a basic implementation
        
        List<User> allUsers = userRepository.findAll();
        long count = 0;
        
        for (User user : allUsers) {
            boolean roleMatch = role == null || user.getRoles().stream()
                    .anyMatch(r -> r.getName().name().equals(role) || 
                                  r.getName().name().equals("ROLE_" + role.toUpperCase()));
            
            boolean statusMatch = status == null || 
                                 (status.equalsIgnoreCase("active") && user.isActive()) ||
                                 (status.equalsIgnoreCase("inactive") && !user.isActive());
            
            boolean searchMatch = search == null || 
                                 user.getUsername().toLowerCase().contains(search.toLowerCase()) ||
                                 user.getEmail().toLowerCase().contains(search.toLowerCase()) ||
                                 (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(search.toLowerCase())) ||
                                 (user.getLastName() != null && user.getLastName().toLowerCase().contains(search.toLowerCase()));
            
            if (roleMatch && statusMatch && searchMatch) {
                count++;
            }
        }
        
        return count;
    }
    
    @Override
    @Transactional
    public User updateUserRole(User user, String roleName) {
        // Clear existing roles
        user.getRoles().clear();
        
        // Add new role
        ERole eRole;
        switch (roleName.toLowerCase()) {
            case "admin":
            case "role_admin":
                eRole = ERole.ROLE_ADMIN;
                break;
            case "organizer":
            case "role_organizer":
                eRole = ERole.ROLE_ORGANIZER;
                break;
            default:
                eRole = ERole.ROLE_USER;
        }
        
        Optional<Role> roleOpt = roleRepository.findByName(eRole);
        if (roleOpt.isPresent()) {
            Set<Role> roles = new HashSet<>();
            roles.add(roleOpt.get());
            user.setRoles(roles);
        }
        
        return userRepository.save(user);
    }
    
    @Override
    @Transactional
    public boolean resetPassword(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Generate a random temporary password
            String tempPassword = UUID.randomUUID().toString().substring(0, 8);
            
            // Update user with new password
            user.setPassword(passwordEncoder.encode(tempPassword));
            userRepository.save(user);
            
            // In a real implementation, this would send an email with the temporary password
            
            return true;
        }
        
        return false;
    }
    
    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}