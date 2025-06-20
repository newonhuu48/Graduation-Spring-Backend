package com.example.graduation.jwt;

import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.security.core.GrantedAuthority;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final String SECRET_KEY = "e86f05e89abca02befb7ebf7875a6e5fe528600c27f4835943ba45b281f3ab45dceb99d8c817bfcaf355f35d58518b6e460b52967b7cedd9a7cd4f084052ea2dfcbe12a38d458813717021535463cac48b58cd8f4896ad258d6ff4b185b64898d849835601a4da94003185f37ae832d525937717bb1f1c5b51167971d5747fe7";
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 hours



    public String generateToken(Authentication auth) {

        //Get User Roles - ROLE_ADMIN, ROLE_USER etc.
        List<String> roles = auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        //Put them in KV array
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", auth.getName()); //Username
        claims.put("roles", roles); //Roles


        return Jwts.builder()
                .setSubject(auth.getName())
                .setClaims(claims)

                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))

                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}