package com.petervekony.letsplay.controller;

import com.petervekony.letsplay.payload.request.UserUpdateRequest;
import com.petervekony.letsplay.security.services.PrincipalData;
import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/api")
public class UserController {

  @Autowired
  UserService userService;

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/users")
  public ResponseEntity<List<UserModel>> getAllUsers(@RequestParam(required = false) String name) {
    try {
      List<UserModel> users = userService.getAllUsers(name);

      if (users.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      return new ResponseEntity<>(users, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PreAuthorize("isAuthenticated()")
  @GetMapping("/users/{id}")
  public ResponseEntity<UserModel> getUserById(@PathVariable("id") String id) {
    Optional<UserModel> userData = userService.getUserById(id);

    return userData
        .map(userModel -> new ResponseEntity<>(userModel, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PreAuthorize("isAuthenticated()")
  @PutMapping("/users/{id}")
  public ResponseEntity<?> updateUser(@PathVariable("id") String id, @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
    PrincipalData principalData = new PrincipalData();
    boolean isSelf = principalData.isSelf(id);

    if (!principalData.authCheck(id)) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    if (principalData.isAdmin() && !userUpdateRequest.getRole().isEmpty()) {
      Optional<UserModel> promotedUser = userService.updateUserRole(id, userUpdateRequest.getRole());
      if (promotedUser.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    }

    return userService.updateUser(id, userUpdateRequest, isSelf);
  }

  @PreAuthorize("isAuthenticated()")
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
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
