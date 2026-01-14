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
        int count = 1;

        if (c.hasDescendant()) {
            // فرع وارث أنثى فقط (بنات بدون أبناء)
            boolean hasFemaleChildOnly = (c.has(HeirType.DAUGHTER) || c.has(HeirType.DAUGHTER_OF_SON))
                    && !c.has(HeirType.SON) && !c.has(HeirType.SON_OF_SON);

            if (hasFemaleChildOnly) {
                // الأب مع البنات فقط: فرض + تعصيب
                return new InheritanceShareDto(
                        HeirType.FATHER,
                        count,
                        null,
                        null,
                        ShareType.TAASIB,
                        FixedShare.SIXTH,
                        "يرث الأب سدس التركة فى حالة وجود الفرع الوارث المؤنث (مثل البنت و بنت الابن و بنت ابن الإبن) لقوله تعالى (وَلأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِنْ كَانَ لَهُ وَلَدٌ) (النساء: 11) .إضافة الى الباقى من التركة (إن تبقى شىء) تعصيبا لأنه أولى رجل ذكر لقولة ﷺ (ألحقوا الفرائض بأهلها فما بقى فهو لأولى رجل ذكر)"
                );
            } else {
                // مع ابن (مع أو بدون بنات): سدس فقط
                return new InheritanceShareDto(
                        HeirType.FATHER,
                        count,
                        null,
                        null,
                        ShareType.FIXED,   //  FIXED فقط
                        FixedShare.SIXTH,
                        "يرث الأب السدس فقط فى حالة وجود الفرع الوارث المذكر (مثل الابن وابن الابن ). قال تعالى (وَلأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِنْ كَانَ لَهُ وَلَدٌ)(النساء: 11)"
                );
            }
        }

        // لا يوجد فرع وارث
        return new InheritanceShareDto(
                HeirType.FATHER,
                count,
                null,
                null,
                ShareType.TAASIB,  // الباقي
                null,
                "يرث الأب الباقى تعصيباً فى حالة عدم الفرع الوارث المذكر والمؤنث . قال ﷺ ( ألحقوا الفرائض بأهلها فما بقى فهو لأولى رجل ذكر.)"
        );
    }
}