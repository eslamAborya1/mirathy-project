package com.NTG.mirathy.DTOs;

import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;

public record InheritanceShareDto(
        HeirType heirType,
        ShareType shareType,
        FixedShare fixedShare,
        TaaasibRule taaasibRule,
        String reason
) {
}
