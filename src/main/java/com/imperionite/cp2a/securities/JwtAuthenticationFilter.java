package com.imperionite.cp2a.securities;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenProvider jwtTokenProvider; // Inject JWT token provider

    @Autowired
    private UserDetailsService userDetailsService; // Inject user details service

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization"); // Get Authorization header
        if (header != null && header.startsWith("Bearer ")) {
            String jwt = header.substring(7); // Extract token
            if (jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsername(jwt); // Get username from token

                UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Load user details
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()); // Create authentication object
                SecurityContextHolder.getContext().setAuthentication(authentication); // Set authentication in context
            }
        }
        filterChain.doFilter(request, response); // Continue with filter chain
    }
}