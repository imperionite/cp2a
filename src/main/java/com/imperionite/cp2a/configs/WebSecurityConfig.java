package com.imperionite.cp2a.configs;

import com.imperionite.cp2a.securities.JwtAuthenticationFilter;
import com.imperionite.cp2a.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity // Enable Spring Security's web security features
public class WebSecurityConfig {

    @Autowired
    CustomUserDetailsService userDetailsService; // Inject custom user details service

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(); // Create JWT authentication filter bean
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager(); // Create authentication manager bean
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Create password encoder bean (BCrypt)
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF (Cross-Site Request Forgery) for stateless API
                .cors(cors -> cors // Enable CORS (Cross-Origin Resource Sharing)
                        .configurationSource(request -> {
                            CorsConfiguration configuration = new CorsConfiguration();
                            configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:3157", "http://yourdomain.com")); // **MODIFY FOR PRODUCTION** - Specify allowed origins
                            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Specify allowed HTTP methods
                            configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept")); // Specify allowed headers
                            configuration.setExposedHeaders(Arrays.asList("Authorization")); // Specify exposed headers (for JWT)
                            configuration.setMaxAge(3600L); // Set max age for preflight requests (1 hour)
                            return configuration;
                        }))
                .sessionManagement(sessionManagement -> // Configure session management
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set session policy to stateless (for JWT)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests // Authorize HTTP requests
                        .requestMatchers("/api/auth/register").hasRole("ADMIN") // Restrict /api/auth/register to ADMIN role
                        .requestMatchers("/api/auth/**").permitAll() // Allow access to other /api/auth endpoints (login, etc.)
                        .anyRequest().authenticated()); // All other requests require authentication

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // Add JWT filter before username/password filter

        return http.build(); // Build and return the SecurityFilterChain
    }
}