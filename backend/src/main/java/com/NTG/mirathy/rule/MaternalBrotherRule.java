package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class MaternalBrotherRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {

        // لو مفيش أخ لأم → القاعدة مش مطبقة
        if (!c.has(HeirType.MATERNAL_BROTHER)) return false;

        // محجوب بالأسلاف أو الفروع الوارثة
        if (c.has(HeirType.FATHER) || c.has(HeirType.GRANDFATHER) || c.has(HeirType.SON) || c.has(HeirType.DAUGHTER) || c.has(HeirType.SON_OF_SON) || c.has(HeirType.DAUGHTER_OF_SON)) {
            return false;
        }

        return true;
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        int maternalBrothers = c.count(HeirType.MATERNAL_BROTHER);
        int maternalSisters = c.count(HeirType.MATERNAL_SISTER);

        // الأخ لأم منفرد أو مع أخواته → السدس إذا واحد، الثلث إذا أكثر
        if (maternalBrothers + maternalSisters == 1) {
            return new InheritanceShareDto(
                    null,
                    HeirType.MATERNAL_BROTHER,
                    ShareType.FIXED,
                    FixedShare.SIXTH,
                    "الأخ لأم منفرد يأخذ السدس إذا لم يكن معه آخرون"
            );
        }

        if (maternalBrothers + maternalSisters > 1) {
            return new InheritanceShareDto(
                    null,
                    HeirType.MATERNAL_BROTHER,
                    ShareType.FIXED,
                    FixedShare.THIRD,
                    "الأخ لأم وأخواته يشتركون في الثلث إذا كانوا أكثر من واحد"
            );
        }

        return null;
    }
}
