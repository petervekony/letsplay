package com.petervekony.letsplay.model;

import com.petervekony.letsplay.security.UserLevel;

import java.util.List;

public class UserModel {
  private String id;
  private String name;
  private String email;
  private String password;
  private String role;
  private List<ProductModel> products;
}
