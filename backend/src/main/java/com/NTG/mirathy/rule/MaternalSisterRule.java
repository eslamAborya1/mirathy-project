package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class MaternalSisterRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {

        // إذا مفيش أخت لأم → القاعدة مش مطبقة
        if (!c.has(HeirType.MATERNAL_SISTER)) return false;

        // تحجبها الحالات التالية
        if (c.has(HeirType.FATHER) || c.has(HeirType.GRANDFATHER) || c.has(HeirType.SON) || c.has(HeirType.SON_OF_SON)) {
            return false;
        }

        return true;
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        int sisters = c.count(HeirType.MATERNAL_SISTER);
        boolean hasMaternalBrother = c.has(HeirType.MATERNAL_BROTHER);

        // السدس إذا كانت منفردة مع أخ لأم واحد فقط
        if (sisters == 1 && !hasMaternalBrother) {
            return new InheritanceShareDto(
                    null,
                    HeirType.MATERNAL_SISTER,
                    ShareType.FIXED,
                    FixedShare.SIXTH,
                    "الأخت لأم تأخذ السدس إذا انفردت ولم يوجد إخوة لأم"
            );
        }

        // إذا كان معها أخ لأم → السدس معاً
        if (hasMaternalBrother) {
            return new InheritanceShareDto(
                    null,
                    HeirType.MATERNAL_SISTER,
                    ShareType.FIXED,
                    FixedShare.SIXTH,
                    "الأخ والأخت لأم يشتركان في السدس عند الانفراد"
            );
        }

        // أكثر من واحدة → الثلث
        if (sisters >= 2) {
            return new InheritanceShareDto(
                    null,
                    HeirType.MATERNAL_SISTER,
                    ShareType.FIXED,
                    FixedShare.THIRD,
                    "الأخوات لأم يشتركن في الثلث إذا كن اثنتين فأكثر"
            );
        }

        return null;
    }
}
