package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class PaternalBrotherRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        if (!c.has(HeirType.PATERNAL_BROTHER)) return false;
        if (c.has(HeirType.FATHER) || c.has(HeirType.SON) ||
                c.has(HeirType.SON_OF_SON) || c.has(HeirType.FULL_BROTHER)) {
            return false;
        }
        return true;
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.PATERNAL_BROTHER;
        int count = c.count(heirType);
        ShareType shareType = ShareType.TAASIB;
        String reason = "الأخ لأب يرث تعصيبًا إذا لم يحجبه الأب أو الابن أو ابن الابن أو الأخ لأبوين";

        return new InheritanceShareDto(
                heirType,
                count,
                null,
                null,
                shareType,
                null,
                reason
        );
    }
}