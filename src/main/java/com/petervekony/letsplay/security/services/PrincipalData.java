package com.petervekony.letsplay.security.services;

import com.petervekony.letsplay.security.services.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class PrincipalData {
    private final boolean isAdmin;
    private final UserDetailsImpl userDetails;

    public PrincipalData() {
        this.userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.isAdmin = userDetails.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("ROLE_admin"));
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public UserDetailsImpl getUserDetails() {
        return this.userDetails;
    }

    public boolean authCheck(String id) {
        return this.isAdmin || this.userDetails.getId().equals(id);
    }
}
