package com.petervekony.letsplay.controller;

import com.petervekony.letsplay.model.ProductModel;
import com.petervekony.letsplay.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// @CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class ProductController {
  @Autowired
  ProductRepository productRepository;

  @GetMapping("/products")
  public ResponseEntity<List<ProductModel>> getAllProducts(@RequestParam(required = false) String name) {
    try {
      List<ProductModel> products = new ArrayList<>();
      if (name == null) {
        productRepository.findAll().forEach(products::add);
      } else {
        productRepository.findByName(name).forEach(products::add);
      }

      if (products.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(products, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/products/{id}")
  public ResponseEntity<ProductModel> getProductById(@PathVariable("id") String id) {
    Optional<ProductModel> productData = productRepository.findById(id);

    return productData
        .map(productModel -> new ResponseEntity<>(productModel, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
