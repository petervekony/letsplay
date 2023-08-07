package com.petervekony.letsplay.controller;

import com.petervekony.letsplay.model.ERole;
import com.petervekony.letsplay.model.Role;
import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  UserRepository userRepository;

  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

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

  @PostMapping("/users")
  public ResponseEntity<UserModel> createUser(@RequestBody UserModel userModel) {
    try {
      // hashing the user password before saving
      String rawPassword = userModel.getPassword();
      String encodedPassword = passwordEncoder.encode(rawPassword);

      UserModel _userModel = userRepository.save(new UserModel(userModel.getName(), userModel.getEmail(), encodedPassword));

      // setting user password to null before sending the response
      _userModel.setPassword(null);

      return new ResponseEntity<>(_userModel, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/users/{id}")
  public ResponseEntity<UserModel> updateUser(@PathVariable("id") String id, @RequestBody UserModel userModel) {
    Optional<UserModel> userData = userRepository.findById(id);

    if (userData.isPresent()) {
      UserModel _user = userData.get();
      _user.setName(userModel.getName());
      _user.setEmail(userModel.getEmail());
      return new ResponseEntity<>(userRepository.save(_user), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<HttpStatus> deleteUser(@PathVariable String id) {
    try {
      userRepository.deleteById(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
