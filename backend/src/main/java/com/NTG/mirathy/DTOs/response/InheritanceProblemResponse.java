package com.NTG.mirathy.DTOs.response;

import java.time.LocalDateTime;

public record InheritanceProblemResponse(
        Long id,
        String title,
        LocalDateTime createdAt,
        boolean isFavorite
) {
}
