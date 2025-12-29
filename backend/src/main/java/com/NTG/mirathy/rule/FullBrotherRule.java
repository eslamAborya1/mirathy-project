package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class FullBrotherRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {

        // محجوب بالأصل الوارث من الذكور
        if (c.has(HeirType.FATHER) || c.has(HeirType.GRANDFATHER)) return false;

        // محجوب بالفرع الوارث الذكر
        if (c.has(HeirType.SON) || c.has(HeirType.SON_OF_SON)) return false;

        return c.has(HeirType.FULL_BROTHER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        // مع أخت شقيقة → تعصيب
        if (c.has(HeirType.FULL_SISTER)) {
            return new InheritanceShareDto(
                    null,
                    HeirType.FULL_BROTHER,
                    ShareType.TAASIB,
                    null,
                    "الأخ الشقيق يرث تعصيبًا مع الأخت الشقيقة للذكر مثل حظ الأنثيين"
            );
        }

        // منفرد أو مع أصحاب فروض → عصبة يأخذ الباقي
        return new InheritanceShareDto(
                null,
                HeirType.FULL_BROTHER,
                ShareType.TAASIB,
                null,
                "الأخ الشقيق من العصبات بالنفس يرث ما بقي بعد أصحاب الفروض"
        );
    }
}
