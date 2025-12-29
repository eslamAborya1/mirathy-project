package com.NTG.mirathy.DTOs.response;

import java.time.LocalDateTime;

public record InheritanceProblemResponse(
        Long id,
        String title,
        Double totalValue,
        LocalDateTime createdAt,
        boolean isFavorite
) {
}
