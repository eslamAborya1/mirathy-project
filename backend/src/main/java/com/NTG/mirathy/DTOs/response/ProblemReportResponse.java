package com.NTG.mirathy.DTOs.response;

import com.NTG.mirathy.Entity.Enum.ProblemReportStauts;


import java.time.LocalDateTime;


public record ProblemReportResponse(
        Long id,
        String description,
        ProblemReportStauts status,
        LocalDateTime createdAt,
        UserResponse user,
        InheritanceProblemResponse problem
) {
}
