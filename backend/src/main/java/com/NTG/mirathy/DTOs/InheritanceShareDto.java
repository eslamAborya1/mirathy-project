package com.NTG.mirathy.DTOs;

import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;

public record InheritanceShareDto(
        HeirType heirType,
        ShareType shareType,
        FixedShare fixedShare,
        String reason
) {
}
