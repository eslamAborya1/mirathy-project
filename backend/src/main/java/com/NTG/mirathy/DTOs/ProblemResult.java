package com.NTG.mirathy.DTOs;

import java.util.List;

public record ProblemResult(
        String title,
        String note,
        List<InheritanceResult>inheritanceResults
) {
}
