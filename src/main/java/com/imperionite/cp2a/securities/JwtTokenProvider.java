package com.imperionite.cp2a.securities;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final Key SECRET_KEY = Keys.hmacShaKeyFor("kaSlbquqbeO8mibzwiIsSnAUqDhXeVRG8FNF+eThd5H/1eotqMdWS9nfhuvnbrMoJLlVNsM3rF".getBytes()); // Secret key for signing
    private final long EXPIRATION_TIME = 3000 * 60 * 60; // Token expiration time (3 hours for dev)

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME); // Set token expiry
        return Jwts.builder() 
                .setSubject(username) // Set token subject (username)
                .setIssuedAt(now) // Set token issued date
                .setExpiration(expiryDate) // Set token expiration
                .signWith(SECRET_KEY) // Sign the token
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token); // Validate token
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false; // Token validation failed
        }
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody(); // Get claims
        return claims.getSubject(); // Return username from claims
    }
}