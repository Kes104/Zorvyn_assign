package com.finance.demo.dto;

public record UserUpdateRequest(
        String name,
        String role,
        String status,
        String password
) {
}
