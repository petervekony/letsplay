package com.petervekony.letsplay.repository;

import com.petervekony.letsplay.model.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository  extends MongoRepository<UserModel, String> {
  List<UserModel> findByName(String name);
  // List<UserModel> findByOwner(String id);
}
