package com.petervekony.letsplay.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "products")
public class ProductModel {
  @Id
  private String id;

  @Field
  @NotNull
  @Size(min = 3, max = 50, message = "Name has to be between 3 and 50 characters long")
  private String name;

  @Field
  @NotNull
  @Size(min = 3, max = 150, message = "Description has to be between 3 and 150 characters long")
  private String description;

  @Field
  @NotNull
  @Min(value = 0, message = "Price must be a positive value or zero")
  private Double price;

  @Field
  private String userId;

  private ProductModel() {}

  public ProductModel(String name, String description, Double price, String userId) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.userId = userId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
