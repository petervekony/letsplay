package com.petervekony.letsplay.security.services;

import org.springframework.security.core.GrantedAuthority;

import org.springframework.security.core.context.SecurityContextHolder;

public class PrincipalData {
    private final boolean isAdmin;
    private final UserDetailsImpl userDetails;

    public PrincipalData() {
        this.userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        this.isAdmin = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> "admin".equals(role));
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
