package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class FullSisterRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {

        // محجوبة بالأصل الوارث من الذكور أو الفرع الوارث
        if (c.has(HeirType.FATHER) || c.has(HeirType.GRANDFATHER)) return false;
        if (c.has(HeirType.SON) || c.has(HeirType.SON_OF_SON)) return false;

        return c.has(HeirType.FULL_SISTER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        int sisters = c.count(HeirType.FULL_SISTER);

        // مع أخ شقيق → تعصيب
        if (c.has(HeirType.FULL_BROTHER)) {
            return new InheritanceShareDto(
                    null,
                    HeirType.FULL_SISTER,
                    ShareType.TAASIB,
                    null,
                    "الأخت الشقيقة ترث تعصيبًا مع الأخ الشقيق"
            );
        }

        // أخت واحدة → النصف
        if (sisters == 1) {
            return new InheritanceShareDto(
                    null,
                    HeirType.FULL_SISTER,
                    ShareType.FIXED,
                    FixedShare.HALF,
                    "للأخت الشقيقة النصف عند الكلالة"
            );
        }

        // أختان فأكثر → الثلثان
        return new InheritanceShareDto(
                null,
                HeirType.FULL_SISTER,
                ShareType.FIXED,
                FixedShare.TWO_THIRDS,
                "للأخوات الشقيقات الثلثان عند الكلالة"
        );
    }
}
