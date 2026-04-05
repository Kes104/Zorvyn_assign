package com.finance.demo.dto;

import com.finance.demo.model.Role;
import com.finance.demo.model.UserStatus;

public record UserResponse(
        Integer id,
        String name,
        String email,
        Role role,
        UserStatus status
) {
}
