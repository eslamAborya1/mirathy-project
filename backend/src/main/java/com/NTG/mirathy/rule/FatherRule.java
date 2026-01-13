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

        // وجود فرع وارث
        if (c.hasDescendant()) {

            // فرع وارث ذكر → سدس فقط
            if (c.hasMaleChild()) {
                return new InheritanceShareDto(
                        HeirType.FATHER,
                        count,
                        null,
                        null,
                        ShareType.FIXED,
                        FixedShare.SIXTH,
                        "يرث الأب السدس فقط فى حالة وجود الفرع الوارث المذكر (مثل الابن وابن الابن ). قال تعالى (وَلأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِنْ كَانَ لَهُ وَلَدٌ)"
                );
            }

            // فرع وارث أنثى فقط → سدس + تعصيب
            return new InheritanceShareDto(
                    HeirType.FATHER,
                    count,
                    null,
                    null,
                    ShareType.TAASIB,
                    FixedShare.SIXTH,
                    "يرث الأب سدس التركة فى حالة وجود الفرع الوارث المؤنث (مثل البنت و بنت الابن و بنت ابن الإبن) لقوله تعالى (وَلأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِنْ كَانَ لَهُ وَلَدٌ) .إضافة الى الباقى من التركة (إن تبقى شىء) تعصيبا لأنه أولى رجل ذكر لقولة ﷺ (ألحقوا الفرائض بأهلها فما بقى فهو لأولى رجل ذكر)"
            );
        }

        // لا يوجد فرع وارث
        return new InheritanceShareDto(
                HeirType.FATHER,
                count,
                null,
                null,
                ShareType.TAASIB,
                null,
                "يرث الأب الباقي تعصيبًا لعدم وجود فرع وارث"
        );
    }
    }
