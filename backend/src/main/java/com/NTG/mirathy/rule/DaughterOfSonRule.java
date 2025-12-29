package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class DaughterOfSonRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {

        // محجوبة بوجود الابن
        if (c.has(HeirType.SON)) return false;

        return c.has(HeirType.DAUGHTER_OF_SON);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        int daughtersOfSon = c.count(HeirType.DAUGHTER_OF_SON);

        // مع وجود ابن ابن → تعصيب
        if (c.has(HeirType.SON_OF_SON)) {
            return new InheritanceShareDto(
                    null,
                    HeirType.DAUGHTER_OF_SON,
                    ShareType.TAASIB,
                    null,
                    "بنت الابن ترث تعصيبًا مع ابن الابن"
            );
        }

        // وجود بنت مباشرة واحدة → السدس تكملة للثلثين
        if (c.count(HeirType.DAUGHTER) == 1) {
            return new InheritanceShareDto(
                    null,
                    HeirType.DAUGHTER_OF_SON,
                    ShareType.FIXED,
                    FixedShare.SIXTH,
                    "بنت الابن ترث السدس تكملة للثلثين مع البنت"
            );
        }

        // وجود بنتين مباشرتين أو أكثر → محجوبة
        if (c.count(HeirType.DAUGHTER) >= 2) {
            return new InheritanceShareDto(
                    0.0,
                    HeirType.DAUGHTER_OF_SON,
                    ShareType.Mahgub,
                    null,
                    "بنت الابن محجوبة لوجود بنتين فأكثر"
            );
        }

        // لا بنات ولا أبناء
        if (daughtersOfSon == 1) {
            return new InheritanceShareDto(
                    null,
                    HeirType.DAUGHTER_OF_SON,
                    ShareType.FIXED,
                    FixedShare.HALF,
                    "لبنت الابن النصف لانفرادها"
            );
        }

        return new InheritanceShareDto(
                null,
                HeirType.DAUGHTER_OF_SON,
                ShareType.FIXED,
                FixedShare.TWO_THIRDS,
                "لبنات الابن الثلثان لاشتراكهن"
        );
    }
}
