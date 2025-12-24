package com.NTG.mirathy.DTOs.response;

import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.InheritanceMember;

import java.util.List;

public record InheritanceMemberResponse(
        HeirType memberType,
        ShareType shareFraction,
        FixedShare fixedShare,
        Double shareValue,
        Integer memberCount,
        String description
) {
}
