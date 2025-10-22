package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.entity.UserRole;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminSetupController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // One-time admin creation endpoint (should be secured or removed in production)
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");
            String username = request.get("username");

            if (email == null || password == null || username == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Email, password, and username are required"));
            }

            // Check if admin already exists
            if (userRepository.existsByEmail(email)) {
                return ResponseEntity.badRequest().body(Map.of("error", "User with this email already exists"));
            }

            // Create admin user
            User admin = new User();
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(UserRole.ADMIN);
            admin.setIsActive(true);

            User saved = userRepository.save(admin);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Admin user created successfully",
                "admin", Map.of(
                    "id", saved.getId(),
                    "username", saved.getUsername(),
                    "email", saved.getEmail(),
                    "role", saved.getRole().name()
                )
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}

