package com.petervekony.letsplay.controller;

import com.petervekony.letsplay.security.services.PrincipalData;
import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  UserService userService;

  @GetMapping("/users")
  public ResponseEntity<List<UserModel>> getAllUsers(@RequestParam(required = false) String name) {
    try {
      List<UserModel> users = userService.getAllUsers(name);

      if (users.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.I_AM_A_TEAPOT);
    }
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<UserModel> getUserById(@PathVariable("id") String id) {
    Optional<UserModel> userData = userService.getUserById(id);

    return userData
        .map(userModel -> new ResponseEntity<>(userModel, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PostMapping("/users")
  public ResponseEntity<UserModel> createUser(@RequestBody UserModel userModel) {
    // TODO: ADMIN CHECK! OR POSSIBLY DELETE, SEE /api/auth/signup
    try {
      UserModel _userModel = userService.createUser(userModel);

      // setting user password to null before sending the response
      _userModel.setPassword(null);

      return new ResponseEntity<>(_userModel, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.I_AM_A_TEAPOT);
    }
  }

  @PutMapping("/users/{id}")
  public ResponseEntity<UserModel> updateUser(@PathVariable("id") String id, @Valid @RequestBody UserModel userModel) {
    PrincipalData principalData = new PrincipalData();
    boolean isSelf = principalData.isSelf(id);

    if (!principalData.authCheck(id)) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    if (principalData.isAdmin() && !userModel.getRole().isEmpty()) {
      Optional<UserModel> promotedUser = userService.updateUserRole(id, userModel.getRole());
      if (promotedUser.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    }

    Optional<UserModel> updatedUser = userService.updateUser(id, userModel, isSelf);
    return updatedUser
            .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("/users/{id}")
  public ResponseEntity<HttpStatus> deleteUser(@PathVariable String id) {
    try {
      PrincipalData principalData = new PrincipalData();

      if (!principalData.authCheck(id)) {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }

      userService.deleteUser(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.I_AM_A_TEAPOT);
    }
  }
}
