package com.petervekony.letsplay.service;

import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.repository.ProductRepository;
import com.petervekony.letsplay.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    public Optional<UserModel> updateUser(String id, UserModel userModel, boolean isSelf) {
        Optional<UserModel> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            UserModel _user = userData.get();
            if (userModel.getName() != null) _user.setName(userModel.getName());
            if (userModel.getEmail() != null) _user.setEmail(userModel.getEmail());
            if (userModel.getPassword() != null) _user.setPassword(passwordEncoder.encode(userModel.getPassword()));

            // clearing the security context if the user changed their own data
            if (isSelf) SecurityContextHolder.clearContext();

            return Optional.of(userRepository.save(_user));
        } else {
            return Optional.empty();
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
