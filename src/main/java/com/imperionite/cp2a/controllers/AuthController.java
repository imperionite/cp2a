package com.imperionite.cp2a.controllers;

import com.imperionite.cp2a.entities.User;
import com.imperionite.cp2a.securities.JwtTokenProvider;
import com.imperionite.cp2a.services.*;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService; // Inject UserService

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody User user,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            if (currentUser.getIsAdmin()) {
                authService.register(user);
                return ResponseEntity.ok("User registered successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        return authService.login(user)
                .map(authenticatedUser -> {
                    String username = authenticatedUser.getUsername();
                    Boolean is_admin = authenticatedUser.getIsAdmin();
                    String jwt = jwtTokenProvider.generateToken(username);
                    String refreshToken = jwtTokenProvider.generateRefreshToken(username);

                    Map<String, String> responseBody = new HashMap<>();
                    responseBody.put("access", jwt);
                    responseBody.put("refresh", refreshToken);
                    responseBody.put("username", username);
                    responseBody.put("is_admin", is_admin.toString());
                    responseBody.put("message", username + " successfully logged in");
                    return ResponseEntity.ok(responseBody);
                })
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials!")));
    }

}