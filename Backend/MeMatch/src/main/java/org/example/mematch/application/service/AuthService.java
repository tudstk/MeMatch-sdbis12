package org.example.mematch.application.service;

import org.example.mematch.domain.entities.User;
import org.example.mematch.infrastructure.persistence.jpa.UserRepository;
import org.example.mematch.infrastructure.security.JwtUtil;
import org.example.mematch.infrastructure.web.dto.AuthRequest;
import org.example.mematch.infrastructure.web.dto.AuthResponse;
import org.example.mematch.infrastructure.web.dto.LoginRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, 
                      PasswordEncoder passwordEncoder,
                      JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(AuthRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        // Encode password
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // Create user
        User user = User.create(
                request.getEmail(),
                request.getUsername(),
                encodedPassword
        );

        user = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return new AuthResponse(token, user);
    }

    public AuthResponse login(LoginRequest request) {
        // Find user by username or email
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByEmail(request.getUsernameOrEmail())
                        .orElseThrow(() -> new BadCredentialsException("Invalid username/email or password")));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username/email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return new AuthResponse(token, user);
    }
}

