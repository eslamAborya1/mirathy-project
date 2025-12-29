package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class GrandmotherMaternalRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {

        // لو مفيش جدة لأم → القاعدة مش مطبقة
        if (!c.has(HeirType.GRANDMOTHER_MATERNAL)) return false;

        // محجوبة بوجود الأم
        if (c.has(HeirType.MOTHER)) return false;

        return true;
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        // وجود الجدة للأب أيضًا → يشتركان في السدس
        if (c.has(HeirType.GRANDMOTHER_PATERNAL)) {
            return new InheritanceShareDto(
                    null,
                    HeirType.GRANDMOTHER_MATERNAL,
                    ShareType.FIXED,
                    FixedShare.SIXTH,
                    "الجدة للأم تشترك مع الجدة للأب في السدس، يُقسم بينهما بالتساوي"
            );
        }

        // إذا كانت وحدها → السدس
        return new InheritanceShareDto(
                null,
                HeirType.GRANDMOTHER_MATERNAL,
                ShareType.FIXED,
                FixedShare.SIXTH,
                "الجدة للأم ترث السدس لعدم وجود الأم"
        );
    }
}
