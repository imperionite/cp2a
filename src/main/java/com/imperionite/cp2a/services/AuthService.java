package com.imperionite.cp2a.services;

import com.imperionite.cp2a.entities.User;
import com.imperionite.cp2a.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> login(User user) {
        return userRepository.findByUsername(user.getUsername())
                .filter(existingUser -> passwordEncoder.matches(user.getPassword(), existingUser.getPassword()));
    }
}
