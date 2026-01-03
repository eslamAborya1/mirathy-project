package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class Grandfather implements InheritanceRule {
    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.GRANDFATHER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase inheritanceCase) {
        HeirType heirType = HeirType.GRANDFATHER;
        String reason = null;
        FixedShare fixedShare = null;
        ShareType shareType = null;
        TaaasibRule taaasibRule = null;

        if (inheritanceCase.has(HeirType.FATHER)) {
            shareType = ShareType.Mahgub;
            reason = "من أدلى الى الميت بواسطة حجبته تلك الواسطة عند وجودها . فأب الأب لايرث فى وجود الأب وأب أب الأب لا يرث فى وجود أب الأب.";
        }else if (inheritanceCase.hasMaleChild()) {
            shareType = ShareType.FIXED;
            fixedShare=FixedShare.SIXTH;
            reason="يرث الجد عند عدم وجود الأب السدس فقط فى حالة وجود الفرع الوارث المذكر (مثل الابن وابن الابن )";
        } else if (!inheritanceCase.hasMaleChild()&&inheritanceCase.hasFemaleChild()) {
            shareType = ShareType.FIXED;
            fixedShare=FixedShare.SIXTH;
            taaasibRule=TaaasibRule.REMAINDER_ONLY;
            reason="يرث الجد عند عدم وجود الأب سدس التركة فى حالة وجود الفرع الوارث المؤنث (مثل البنت و بنت الابن و بنت ابن الإبن) لقوله تعالى (وَلأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِنْ كَانَ لَهُ وَلَدٌ) .إضافة الى الباقى من التركة (إن تبقى شىء) تعصيبا لأنه أولى رجل ذكر لقولة ﷺ (ألحقوا الفرائض بأهلها فما بقى فهو لأولى رجل ذكر)";
        }else {
            shareType = ShareType.TAASIB;
            taaasibRule=TaaasibRule.REMAINDER_ONLY;
            reason="يرث الجد عند عدم وجود الأب الباقى تعصيباً فى حالة عدم الفرع الوارث المذكر والمؤنث . قال ﷺ ( ألحقوا الفرائض بأهلها فما بقى فهو لأولى رجل ذكر.)";
        }

        return new InheritanceShareDto(heirType, shareType, fixedShare, taaasibRule, reason);
    }
}
