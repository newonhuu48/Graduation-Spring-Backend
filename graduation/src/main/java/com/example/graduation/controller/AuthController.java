package com.example.graduation.controller;

import com.example.graduation.jwt.JwtUtil;
import com.example.graduation.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@RestController("/auth")
public class AuthController {

    private AuthenticationManager authenticationManager;

    private CustomUserDetailsService userDetailsService;

    private JwtUtil jwtUtil;


    @PostMapping("/login")
    public String createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        return jwtUtil.generateToken(userDetails.getUsername());
    }


    //@Data means - Getters + Setters + toString() + Equals() + HashCode()
    @Data
    private static class AuthRequest {
        private String username;
        private String password;

    }
}
