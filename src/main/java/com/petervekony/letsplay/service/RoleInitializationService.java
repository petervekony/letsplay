package com.petervekony.letsplay.service;

import com.petervekony.letsplay.model.ERole;
import com.petervekony.letsplay.model.Role;
import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.repository.RoleRepository;
import com.petervekony.letsplay.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RoleInitializationService {

    @Autowired
    private RoleRepository roleRepository;

    @PostConstruct
    public void initRoles() {
        for (ERole roleName : ERole.values()) {
            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
                System.out.println("Role " + roleName + " created");
            }
        }
    }
}
