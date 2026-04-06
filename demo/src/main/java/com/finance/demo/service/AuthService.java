package com.finance.demo.service;

import com.finance.demo.dto.AuthResponse;
import com.finance.demo.dto.LoginRequest;
import com.finance.demo.dto.RegisterRequest;
import com.finance.demo.dto.UserResponse;
import com.finance.demo.exception.BadRequestException;
import com.finance.demo.model.Role;
import com.finance.demo.model.User;
import com.finance.demo.model.UserStatus;
import com.finance.demo.repository.UserRepository;
import com.finance.demo.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public String checkUserStatus(String adminEmail, String targetUserEmail) {
        // 1. Verify the requester is actually an Admin
        User requester = userRepository.findByEmail(adminEmail).orElse(null);

        if (requester == null || requester.getRole() != Role.ADMIN) {
            return "Access Denied: Only Admins can check user status.";
        }

        // 2. Find the target user
        Optional<User> targetUser = userRepository.findByEmail(targetUserEmail);

        // 3. Return the status
        return targetUser
                .map(user -> "Status for " + targetUserEmail + " is: " + user.getStatus())
                .orElse("User not found.");
    }

    public UserResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email already exists");
        }

        String requestedRole = request.role();
        Role parsedRole = Role.from(requestedRole, null);
        if (requestedRole != null && !requestedRole.isBlank() && parsedRole == null) {
            throw new BadRequestException("Invalid role. Allowed: ADMIN, ANALYST, VIEWER");
        }

        Role assignedRole = (parsedRole != null)
                ? parsedRole
                : (userRepository.count() == 0 ? Role.ADMIN : Role.VIEWER);
        User user = new User(
                request.name().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.password()),
                assignedRole,
                UserStatus.ACTIVE
        );

        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole(), saved.getStatus());
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail).orElse(null);

        if (user == null) return AuthResponse.failure("User not found");
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            return AuthResponse.failure("Invalid password");
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            return AuthResponse.failure("Account is currently " + user.getStatus());
        }

        String message = (user.getRole() == Role.ADMIN)
                ? "Admin login successful. You have dashboard access."
                : "Login successful as " + user.getRole();

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return AuthResponse.success(message, token, user.getRole().name());
    }

}
