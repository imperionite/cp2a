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
    // store this secret keys (for signing in) in .env on production
    private final Key SECRET_KEY = Keys
            .hmacShaKeyFor("kaSlbquqbeO8mibzwiIsSnAUqDhXeVRG8FNF+eThd5H/1eotqMdWS9nfhuvnbrMoJLlVNsM3rF".getBytes());

    // milliseconds = hours × 60 (minutes/hour) × 60 (seconds/minute) × 1000
    // (milliseconds/second)
    private static final long MILLIS_PER_HOUR = 60 * 60 * 1000;
    private final long EXPIRATION_TIME = 1 * MILLIS_PER_HOUR; // 1 hour

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

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME * 2); // Double the expiration for refresh token
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SECRET_KEY)
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
        Claims claims = Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody(); // Get
                                                                                                                // claims
        return claims.getSubject(); // Return username from claims
    }
}