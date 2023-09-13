package com.petervekony.letsplay.service;

import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.payload.request.UserUpdateRequest;
import com.petervekony.letsplay.payload.response.MessageResponse;
import com.petervekony.letsplay.repository.ProductRepository;
import com.petervekony.letsplay.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import com.petervekony.letsplay.security.services.UserDetailsServiceImpl;
import com.petervekony.letsplay.util.EmailValidator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;

    @Autowired
    public ProductRepository productRepository;

    @Autowired
    public AuthService authService;

    @Autowired
    public UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    public List<UserModel> getAllUsers(String name) {
        List<UserModel> users = new ArrayList<>();
        if (name == null) {
            userRepository.findAll().forEach(users::add);
        } else {
            userRepository.findByName(name).forEach(users::add);
        }
        return users;
    }

    public Optional<UserModel> getUserById(String id) {
        return userRepository.findById(id);
    }

    public ResponseEntity<?> updateUser(String id, UserUpdateRequest userUpdateRequest, boolean isSelf) {
        Optional<UserModel> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            UserModel _user = userData.get();

            // if username is not updated, ignore it
            if (userUpdateRequest.getName() != null) {
                if (userRepository.existsByName(userUpdateRequest.getName())) {
                    return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Username is already taken!"));
                }
                _user.setName(userUpdateRequest.getName());
            };

            // if email is not updated, or it is the same as before, ignore it
            if (userUpdateRequest.getEmail() != null && !_user.getEmail().equals(userUpdateRequest.getEmail())) {
                userUpdateRequest.setEmail(userUpdateRequest.getEmail().toLowerCase(Locale.ROOT));
                if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
                    return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Email is already in use!"));
                }
                if (!EmailValidator.isValidEmail(userUpdateRequest.getEmail())) {
                    return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Invalid email format"));
                }
                _user.setEmail(userUpdateRequest.getEmail());
            }

            // if password is not updated, ignore it
            if (userUpdateRequest.getPassword() != null) _user.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));

            UserModel savedUser = userRepository.save(_user);

            // if the user name was updated, user has to reauthenticate, otherwise it works
            if (isSelf && !savedUser.getName().equals(userUpdateRequest.getName())) {
                UserDetails newDetails = userDetailsServiceImpl.loadUserById(savedUser.getId());
                UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(newDetails, null, newDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            }

            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public Optional<UserModel> updateUserRole(String id, String role) {
        Optional<UserModel> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            UserModel _user = userData.get();
            _user.setRole(role);

            UserModel updatedUser = userRepository.save(_user);

            if (SecurityContextHolder.getContext().getAuthentication().getName().equals(_user.getName())) {
                // update SecurityContextHolder with new details
                UserDetails newDetails = userDetailsServiceImpl.loadUserById(updatedUser.getId());
                UsernamePasswordAuthenticationToken newAuthentication = new UsernamePasswordAuthenticationToken(newDetails, null, newDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            }

            return Optional.of(updatedUser);
        } else {
            return Optional.empty();
        }
    }

    public void deleteUser(String id) {
        productRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }


    @PostConstruct
    public void initDefaultAdmin() {
        List<UserModel> optionalAdmin = userRepository.findByName(adminUsername);
        if (!optionalAdmin.isEmpty()) {
            return;
        }

        // Create admin user
        UserModel adminUser = new UserModel();
        adminUser.setName(adminUsername);
        adminUser.setEmail(adminEmail);
        adminUser.setPassword(passwordEncoder.encode(adminPassword));
        adminUser.setRole("admin");

        userRepository.save(adminUser);
    }
}
