package com.NTG.mirathy.DTOs.response;

import com.NTG.mirathy.Entity.Enum.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String fullName,
        String email,
        Role role,
        boolean isActive,
        LocalDateTime createdAt
) {
}
