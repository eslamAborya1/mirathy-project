package com.NTG.mirathy.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ProblemReportRequest(
        Long problemId,
        @NotBlank
        @NotEmpty
        String description
) {
}
