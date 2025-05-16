package com.adamant.storemicroservice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adamant.storemicroservice.models.AuthenticationRequest;
import com.adamant.storemicroservice.models.AuthenticationResponse;
import com.adamant.storemicroservice.services.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest req) {
        log.info("Login attempt for user {}", req.getUsername());
        try {
            var auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.getUsername(), req.getPassword()));
            String token = jwtService.generateToken((UserDetails) auth.getPrincipal());

            // ←---- ADD THIS LINE
            log.debug("Generated JWT for {}: {}", req.getUsername(), token);

            log.info("Login successful for {}; issuing JWT", req.getUsername());
            return ResponseEntity.ok(new AuthenticationResponse(token));
        } catch (AuthenticationException ex) {
            log.warn("Login failed for {}: {}", req.getUsername(), ex.getMessage());
            throw ex;
        }
    }

}
