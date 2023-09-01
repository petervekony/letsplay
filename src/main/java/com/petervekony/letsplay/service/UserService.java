package com.petervekony.letsplay.service;

import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.payload.response.MessageResponse;
import com.petervekony.letsplay.repository.ProductRepository;
import com.petervekony.letsplay.repository.UserRepository;
import com.petervekony.letsplay.util.EmailValidator;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            userRepository.findAll().forEach(user -> {
                user.setPassword(null);
                users.add(user);
            });
        } else {
            userRepository.findByName(name).forEach(user -> {
                user.setPassword(null);
                users.add(user);
            });
        }
        return users;
    }

    public Optional<UserModel> getUserById(String id) {
        Optional<UserModel> user = userRepository.findById(id);
        if (user.isPresent()) {
            user.get().setPassword(null);
            return user;
        } else {
            return Optional.empty();
        }
    }

    public UserModel createUser(UserModel userModel) {
        // hashing the user password before saving
        String rawPassword = userModel.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        userModel.setPassword(encodedPassword);
        userModel.setRole("user");

        return userRepository.save(userModel);
    }

    public ResponseEntity<?> updateUser(String id, UserModel userModel, boolean isSelf) {
        Optional<UserModel> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            UserModel _user = userData.get();
            if (userModel.getName() != null) {
                if (userRepository.existsByName(userModel.getName())) {
                    return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Username is already taken!"));
                }
                _user.setName(userModel.getName());
            };
            if (userModel.getEmail() != null) {
                if (userRepository.existsByEmail(userModel.getEmail())) {
                    return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Email is already in use!"));
                }
                if (!EmailValidator.isValidEmail(userModel.getEmail())) {
                    return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Invalid email format"));
                }
                _user.setEmail(userModel.getEmail());
            }
            if (userModel.getPassword() != null) _user.setPassword(passwordEncoder.encode(userModel.getPassword()));

            // clearing the security context if the user changed their own data
            if (isSelf) SecurityContextHolder.clearContext();

            UserModel savedUser = userRepository.save(_user);
            savedUser.setPassword(null);

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
            return Optional.of(userRepository.save(_user));
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
