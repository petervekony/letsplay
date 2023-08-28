package com.petervekony.letsplay.service;

import com.petervekony.letsplay.model.ProductModel;
import com.petervekony.letsplay.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    public ProductRepository productRepository;

    public List<ProductModel> getAllProducts(String name) {
        List<ProductModel> products = new ArrayList<>();
        if (name == null) {
            productRepository.findAll().forEach(products::add);
        } else {
            productRepository.findByName(name).forEach(products::add);
        }
        return products;
    }

    public Optional<ProductModel> getProductById(String id) {
        return productRepository.findById(id);
    }

    public ProductModel createProduct(ProductModel productModel) {
        return productRepository.save(new ProductModel(productModel.getName(), productModel.getDescription(), productModel.getPrice(), productModel.getUserId()));
    }

    public Optional<ProductModel> updateProduct(String id, ProductModel productModel) {
        Optional<ProductModel> productData = productRepository.findById(id);

        if (productData.isPresent()) {
            ProductModel _product = productData.get();
            _product.setName(productModel.getName());
            _product.setDescription(productModel.getDescription());
            _product.setPrice(productModel.getPrice());
            return Optional.of(productRepository.save(_product));
        } else {
            return Optional.empty();
        }
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
}
