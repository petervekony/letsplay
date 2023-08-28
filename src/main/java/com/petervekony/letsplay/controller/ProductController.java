package com.petervekony.letsplay.controller;

import com.petervekony.letsplay.model.ProductModel;
import com.petervekony.letsplay.repository.ProductRepository;
import com.petervekony.letsplay.security.services.UserDetailsImpl;
import com.petervekony.letsplay.service.ProductService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class ProductController {
  @Autowired
  ProductService productService;

  @GetMapping("/products")
  public ResponseEntity<List<ProductModel>> getAllProducts(@RequestParam(required = false) String name) {
    try {
      List<ProductModel> products = productService.getAllProducts(name);

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
    Optional<ProductModel> productData = productService.getProductById(id);

    return productData
        .map(productModel -> new ResponseEntity<>(productModel, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping("/products")
  public ResponseEntity<ProductModel> createProduct(@RequestBody ProductModel productModel) {
    try {
      UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

      productModel.setUserId(userDetails.getId());

      ProductModel _productModel = productService.createProduct(productModel);

      return new ResponseEntity<>(_productModel, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/products/{id}")
  public ResponseEntity<ProductModel> updateProduct(@PathVariable("id") String id, @RequestBody ProductModel productModel) {
    Optional<ProductModel> updatedProduct = productService.updateProduct(id, productModel);

    return updatedProduct
            .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("products/{id}")
  public ResponseEntity<HttpStatus> deleteProduct(@PathVariable String id) {
    try {
      productService.deleteProduct(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
