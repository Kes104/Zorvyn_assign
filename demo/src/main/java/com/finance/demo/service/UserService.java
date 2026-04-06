package com.finance.demo.service;

import com.finance.demo.dto.UserCreateRequest;
import com.finance.demo.dto.UserResponse;
import com.finance.demo.dto.UserUpdateRequest;
import com.finance.demo.exception.BadRequestException;
import com.finance.demo.exception.ResourceNotFoundException;
import com.finance.demo.model.Role;
import com.finance.demo.model.User;
import com.finance.demo.model.UserStatus;
import com.finance.demo.repository.UserRepository;
import com.finance.demo.security.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponse(user);
    }

    public UserResponse getCurrentUser() {
        String email = SecurityUtils.currentEmail();
        if (email == null) {
            throw new ResourceNotFoundException("Current user not found");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toResponse(user);
    }

    public UserResponse create(UserCreateRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email already exists");
        }
        Role role = resolveRole(request.role(), Role.VIEWER);
        UserStatus status = resolveStatus(request.status(), UserStatus.ACTIVE);
        User user = new User(
                request.name().trim(),
                normalizedEmail,
                passwordEncoder.encode(request.password()),
                role,
                status
        );
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse update(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name().trim());
        }
        if (request.role() != null) {
            user.setRole(resolveRole(request.role(), user.getRole()));
        }
        if (request.status() != null) {
            user.setStatus(resolveStatus(request.status(), user.getStatus()));
        }
        if (request.password() != null && !request.password().isBlank()) {
            if (request.password().length() < 8) {
                throw new BadRequestException("Password must be at least 8 characters");
            }
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public void deactivate(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    public UserResponse toggleStatus(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setStatus(user.getStatus() == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getStatus());
    }

    private Role resolveRole(String roleValue, Role fallback) {
        if (roleValue == null) {
            return fallback;
        }
        Role parsed = Role.from(roleValue, null);
        if (parsed == null) {
            throw new BadRequestException("Invalid role value");
        }
        return parsed;
    }

    private UserStatus resolveStatus(String statusValue, UserStatus fallback) {
        if (statusValue == null) {
            return fallback;
        }
        UserStatus parsed = UserStatus.from(statusValue, null);
        if (parsed == null) {
            throw new BadRequestException("Invalid status value");
        }
        return parsed;
    }
}
