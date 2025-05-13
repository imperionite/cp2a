package com.imperionite.cp2a.services;

/**
 * This code checks the user.isAdmin() value.  If it's true, it grants the user the "ROLE_ADMIN" authority.  
 * If it's false, the user gets no authorities (or you can add other authorities as needed).  
 * This is the crucial part that connects isAdmin field to Spring Security's authorization mechanism.
 * 
 * Spring Security's hasRole() method checks if the user has a specific authority.  By default, it prefixes roles with "ROLE_".  
 * in CustomUserDetailsService, creating a SimpleGrantedAuthority("ROLE_ADMIN") if user.isAdmin() is true.  This is what makes the connection.  
 * When Spring Security checks hasRole("ADMIN"), it's effectively checking if the user has the "ROLE_ADMIN" authority.
 */

import com.imperionite.cp2a.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getIsAdmin() ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                : Collections.emptyList()))
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
    }
}