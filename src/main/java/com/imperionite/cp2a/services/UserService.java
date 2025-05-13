package com.imperionite.cp2a.services;

import com.imperionite.cp2a.entities.User;
import com.imperionite.cp2a.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username); // No need for Optional.ofNullable
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
}