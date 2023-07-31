package com.petervekony.letsplay.controller;

import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.repository.UserRepository;
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
public class UserController {

  @Autowired
  UserRepository userRepository;

  @GetMapping("/users")
  public ResponseEntity<List<UserModel>> getAllUsers(@RequestParam(required = false) String name) {
    try {
      List<UserModel> users = new ArrayList<>();
      if (name == null) {
        userRepository.findAll().forEach(users::add);
      } else {
        userRepository.findByName(name).forEach(users::add);
      }

      if (users.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<UserModel> getUserById(@PathVariable("id") String id) {
    Optional<UserModel> userData = userRepository.findById(id);

    return userData
        .map(userModel -> new ResponseEntity<>(userModel, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
