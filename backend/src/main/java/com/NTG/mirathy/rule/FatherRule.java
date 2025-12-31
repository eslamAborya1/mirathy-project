package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class FatherRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.FATHER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.FATHER;
        int count = c.count(heirType);
        ShareType shareType = null;
        FixedShare fixedShare = null;
        String reason = "";

        if (c.hasDescendant()) {
            shareType = ShareType.FIXED;
            fixedShare = FixedShare.SIXTH;
            reason = "يرث الأب السدس فقط فى حالة وجود الفرع الوارث المذكر (مثل الابن وابن الابن ). قال تعالى (وَلأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِنْ كَانَ لَهُ وَلَدٌ)";
        } else {
            shareType = ShareType.TAASIB;
            reason = "الأب يرث تعصيبًا بعد أصحاب الفروض";
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