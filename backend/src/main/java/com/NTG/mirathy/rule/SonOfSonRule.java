package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class SonOfSonRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        if (c.has(HeirType.SON)) return false;
        return c.has(HeirType.SON_OF_SON);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.SON_OF_SON;
        int count = c.count(heirType);
        ShareType shareType ;
        String reason ;

        if (c.has(HeirType.DAUGHTER)) {
            shareType = ShareType.TAASIB; // يرث الباقي بعد فرض البنت
            reason = "ابن الابن يرث الباقي بعد فرض البنت لأنه أقل درجة منها";
        }
        else {
            shareType = ShareType.TAASIB;
            reason = "ابن الابن يرث تعصيبًا لعدم وجود الابن أو البنت الصلب";
        }
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