package com.petervekony.letsplay.repository;

import java.util.Optional;

import com.petervekony.letsplay.model.ERole;
import com.petervekony.letsplay.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
