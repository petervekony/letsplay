package com.petervekony.letsplay.repository;

import com.petervekony.letsplay.model.ProductModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductModel, String> {
  List<ProductModel> findByName(String name);
  void deleteAllByUserId(String userId);
  // List<ProductModel> findByOwner(String userId);
}
