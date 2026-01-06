package com.NTG.mirathy.DTOs.response;

import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;

public record InheritanceMemberResponse(
        HeirType heirType,
        ShareType shareType,
        FixedShare fixedShare,
        Double shareValue,
        Integer memberCount,
        String reason
) {
}
