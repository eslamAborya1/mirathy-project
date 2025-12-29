package com.NTG.mirathy.DTOs;

import java.util.List;

public record InheritanceCalculationResult(
        String title,
        String note,
        List<InheritanceShareDto> shares
) {
}
