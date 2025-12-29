package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class GrandmotherPaternalRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {

        // لو مفيش جدة لأب → القاعدة مش مطبقة
        if (!c.has(HeirType.GRANDMOTHER_PATERNAL)) return false;

        // محجوبة بوجود الأب أو الجد للأب
        if (c.has(HeirType.FATHER) || c.has(HeirType.GRANDFATHER)) return false;

        return true;
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        // وجود جدة لأم أيضًا → كلاهما يشتركان في السدس
        if (c.has(HeirType.GRANDMOTHER_MATERNAL)) {
            return new InheritanceShareDto(
                    null,
                    HeirType.GRANDMOTHER_PATERNAL,
                    ShareType.FIXED,
                    FixedShare.SIXTH,
                    "الجدة لأب تشترك مع الجدة لأم في السدس"
            );
        }

        // إذا كانت وحدها → السدس
        return new InheritanceShareDto(
                null,
                HeirType.GRANDMOTHER_PATERNAL,
                ShareType.FIXED,
                FixedShare.SIXTH,
                "الجدة لأب ترث السدس لعدم وجود الأب أو الجد للأب"
        );
    }
}
