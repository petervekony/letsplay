package com.petervekony.letsplay.service;

import com.petervekony.letsplay.model.UserModel;
import com.petervekony.letsplay.payload.request.LoginRequest;
import com.petervekony.letsplay.payload.request.SignupRequest;
import com.petervekony.letsplay.payload.response.MessageResponse;
import com.petervekony.letsplay.payload.response.UserInfoResponse;
import com.petervekony.letsplay.repository.UserRepository;
import com.petervekony.letsplay.security.jwt.JwtUtils;
import com.petervekony.letsplay.security.services.UserDetailsImpl;
import com.petervekony.letsplay.util.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getName(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUserId(userDetails.getId());

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority())
                .orElse("user");

        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + jwtToken)
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        role,
                        jwtToken));
    }

    public ResponseEntity<?> registerUser(SignupRequest signupRequest) {
        if (userRepository.existsByName(signupRequest.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        signupRequest.setEmail(signupRequest.getEmail().toLowerCase(Locale.ROOT));
        if (!EmailValidator.isValidEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Invalid email format"));
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        UserModel user =
                new UserModel(
                        signupRequest.getName(),
                        signupRequest.getEmail(),
                        encoder.encode(signupRequest.getPassword()));
        user.setRole("user");
        userRepository.save(user);

        return new ResponseEntity<>(new MessageResponse("User registered successfully!"), HttpStatus.CREATED);
    }
}
