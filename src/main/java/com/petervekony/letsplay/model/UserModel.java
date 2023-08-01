package com.petervekony.letsplay.model;

import com.petervekony.letsplay.security.UserLevel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class UserModel {
  @Id
  private String id;
  private String name;
  private String email;
  private String password;
  private String role;

  private UserModel() {}

  public UserModel(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = UserLevel.user.toString();
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getRole() {
    return role;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
