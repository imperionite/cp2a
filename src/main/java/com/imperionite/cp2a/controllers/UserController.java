package com.imperionite.cp2a.controllers;

import com.imperionite.cp2a.dtos.UserResponseDTO;
import com.imperionite.cp2a.entities.User;
import com.imperionite.cp2a.services.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

@RequestMapping("/api/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Fetch additional user details from the database
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();
            UserResponseDTO response = new UserResponseDTO(currentUser.getId(), currentUser.getUsername(),
                    currentUser.getIsActive(), currentUser.getIsAdmin());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Return 404 if user not found
        }
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> allUsers(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Fetch the user details from the service
        Optional<User> optionalUser = userService.findByUsername(userDetails.getUsername());

        if (optionalUser.isPresent()) {
            User currentUser = optionalUser.get();

            // Check if the user is an admin
            if (currentUser.getIsAdmin()) {
                List<User> users = userService.allUsers(); // Fetch all users
                return ResponseEntity.ok(users);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN) // 403 Forbidden if not an admin
                        .body(List.of());
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // Unauthorized if user doesn't exist
        }
    }
}
