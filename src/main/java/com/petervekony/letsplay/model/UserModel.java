package com.petervekony.letsplay.model;

import org.springframework.data.annotation.Id;

public class UserModel {
  @Id
  private String id;
  private String name;
  private String email;
  private String password;
  private String role;

  public UserModel(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = "user";
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
}
