package com.petervekony.letsplay.service;

import com.petervekony.letsplay.model.ERole;
import com.petervekony.letsplay.model.Role;
import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.repository.ProductRepository;
import com.petervekony.letsplay.repository.RoleRepository;
import com.petervekony.letsplay.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;

    @Autowired
    public RoleRepository roleRepository;

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
            userRepository.findAll().forEach(users::add);
        } else {
            userRepository.findByName(name).forEach(users::add);
        }
        return users;
    }

    public Optional<UserModel> getUserById(String id) {
        return userRepository.findById(id);
    }

    public UserModel createUser(UserModel userModel) {
        // hashing the user password before saving
        String rawPassword = userModel.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);

        return userRepository.save(new UserModel(userModel.getName(), userModel.getEmail(), encodedPassword));
    }

    public Optional<UserModel> updateUser(String id, UserModel userModel) {
        Optional<UserModel> userData = userRepository.findById(id);
        if (userData.isPresent()) {
            UserModel _user = userData.get();
            _user.setName(userModel.getName());
            _user.setEmail(userModel.getEmail());
            return Optional.of(userRepository.save(_user));
        } else {
            return Optional.empty();
        }
    }

    public void deleteUser(String id) {
        productRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }

    public void setRoles(UserModel user, Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(ERole.user)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.admin)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.user)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);
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

        Set<String> adminRoles = new HashSet<>();
        adminRoles.add("admin");
        adminRoles.add("user");
        setRoles(adminUser, adminRoles);

        userRepository.save(adminUser);
    }
}
