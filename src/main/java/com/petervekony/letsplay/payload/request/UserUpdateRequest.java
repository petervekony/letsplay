package com.petervekony.letsplay.payload.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
  @Size(min=3, max=40, message="Error: Name has to be between 3 and 40 characters long")
  private String name;

  @Email(message = "Invalid email format")
  @Size(min=0, max=320, message = "Invalid email length")
  private String email;

  private String password;
  private String role;

  public UserUpdateRequest() {}

  public UserUpdateRequest(String name, String email, String password, String role) {
    this.name = name;
    this.email = email;
    this.password = password;
    this.role = role;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
