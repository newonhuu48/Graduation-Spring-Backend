package com.example.graduation.controller;

import com.example.graduation.jwt.JwtUtil;
import com.example.graduation.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;

    private CustomUserDetailsService userDetailsService;

    private JwtUtil jwtUtil;


    @PostMapping("/login")
    public String createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {

        try {
            // Authenticate and get Authentication object (with roles)
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );

            // Generate token with Authentication (includes roles)
            return jwtUtil.generateToken(authentication);

        } catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }

    }


    //@Data means - Getters + Setters + toString() + Equals() + HashCode()
    @Data
    private static class AuthRequest {
        private String username;
        private String password;

    }
}
