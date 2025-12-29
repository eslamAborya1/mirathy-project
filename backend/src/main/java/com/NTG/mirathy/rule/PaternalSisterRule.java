package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class PaternalSisterRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {

        // لو مفيش أخت لأب → القاعدة مش مطبقة
        if (!c.has(HeirType.PATERNAL_SISTER)) return false;

        // تحجبها الحالات التالية
        if (c.has(HeirType.FATHER) || c.has(HeirType.SON) || c.has(HeirType.SON_OF_SON) || c.has(HeirType.FULL_BROTHER)) {
            return false;
        }

        return true;
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        int sisters = c.count(HeirType.PATERNAL_SISTER);
        boolean hasFullSister = c.has(HeirType.FULL_SISTER);
        boolean hasPaternalBrothers = c.has(HeirType.PATERNAL_BROTHER);

        // إذا كانت مع شقيقة → التعصيب بعد فرض الشقيقة
        if (hasFullSister) {
            return new InheritanceShareDto(
                    null,
                    HeirType.PATERNAL_SISTER,
                    ShareType.TAASIB,
                    null,
                    "الأخت لأب ترث تعصيبًا بعد فرض الأخت الشقيقة"
            );
        }

        // إذا كانت مع إخوة لأب → التعصيب
        if (hasPaternalBrothers) {
            return new InheritanceShareDto(
                    null,
                    HeirType.PATERNAL_SISTER,
                    ShareType.TAASIB,
                    null,
                    "الأخت لأب ترث تعصيبًا مع الإخوة لأب"
            );
        }

        // فرض: واحدة → نصف
        if (sisters == 1) {
            return new InheritanceShareDto(
                    null,
                    HeirType.PATERNAL_SISTER,
                    ShareType.FIXED,
                    FixedShare.HALF,
                    "الأخت لأب تأخذ نصف إذا انفردت"
            );
        }

        // فرض: اثنتان فأكثر → الثلثان
        return new InheritanceShareDto(
                null,
                HeirType.PATERNAL_SISTER,
                ShareType.FIXED,
                FixedShare.TWO_THIRDS,
                "الأخوات لأب يأخذن الثلثين إذا كن اثنتين فأكثر"
        );
    }
}
