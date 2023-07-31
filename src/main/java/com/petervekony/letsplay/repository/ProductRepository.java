package com.petervekony.letsplay.repository;

import com.petervekony.letsplay.model.ProductModel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductModel, String> {
  List<ProductModel> findByName(String name);
  List<ProductModel> findByOwner(String userId);
}
