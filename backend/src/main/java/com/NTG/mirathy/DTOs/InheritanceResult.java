package com.NTG.mirathy.DTOs;

import com.NTG.mirathy.Entity.Enum.HeirType;

public record InheritanceResult(
        String heirType,
        String nasib,
        int countMembers,
        String nisbtElfard,
        Double individualAmount,
        String reason
) {
}
