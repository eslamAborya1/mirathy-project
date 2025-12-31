package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class PaternalSisterRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        if (!c.has(HeirType.PATERNAL_SISTER)) return false;
        if (c.has(HeirType.FATHER) || c.has(HeirType.SON) ||
                c.has(HeirType.SON_OF_SON) || c.has(HeirType.FULL_BROTHER)) {
            return false;
        }
        return true;
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.PATERNAL_SISTER;
        int count = c.count(heirType);
        ShareType shareType = null;
        FixedShare fixedShare = null;
        String reason = "";

        if (c.has(HeirType.FULL_SISTER) || c.has(HeirType.PATERNAL_BROTHER)) {
            shareType = ShareType.TAASIB;
            reason = "الأخت لأب ترث تعصيبًا مع الإخوة";
        } else if (count == 1) {
            shareType = ShareType.FIXED;
            fixedShare = FixedShare.HALF;
            reason = "الأخت لأب تأخذ نصف إذا انفردت";
        } else {
            shareType = ShareType.FIXED;
            fixedShare = FixedShare.TWO_THIRDS;
            reason = "الأخوات لأب يأخذن الثلثين إذا كن اثنتين فأكثر";
        }

        return new InheritanceShareDto(
                heirType,
                count,
                null,
                null,
                shareType,
                fixedShare,
                reason
        );
    }
}