package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.Entity.InheritanceMember;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class FatherRule implements InheritanceRule {


    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.FATHER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase inheritanceCase) {
        HeirType heirType = HeirType.FATHER;
        ShareType shareType = null;
        FixedShare fixedShare = null;
        TaaasibRule taaasibRule = null;
        String reason = null;
        if (inheritanceCase.hasMaleChild()) {
             shareType = ShareType.FIXED;
             fixedShare=FixedShare.SIXTH;
             reason="يرث الأب السدس فقط فى حالة وجود الفرع الوارث المذكر (مثل الابن وابن الابن ). قال تعالى (وَلأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِنْ كَانَ لَهُ وَلَدٌ)";
        } else if (!inheritanceCase.hasMaleChild()&&inheritanceCase.hasFemaleChild()) {
            shareType = ShareType.FIXED;
            fixedShare=FixedShare.SIXTH;
            taaasibRule=TaaasibRule.REMAINDER_ONLY;
            reason="يرث الأب سدس التركة فى حالة وجود الفرع الوارث المؤنث (مثل البنت و بنت الابن و بنت ابن الإبن) لقوله تعالى (وَلأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِنْ كَانَ لَهُ وَلَدٌ) .إضافة الى الباقى من التركة (إن تبقى شىء) تعصيبا لأنه أولى رجل ذكر لقولة ﷺ (ألحقوا الفرائض بأهلها فما بقى فهو لأولى رجل ذكر)";
        }else {
            shareType = ShareType.TAASIB;
            taaasibRule=TaaasibRule.REMAINDER_ONLY;
            reason="يرث الأب الباقى تعصيباً فى حالة عدم الفرع الوارث المذكر والمؤنث . قال ﷺ ( ألحقوا الفرائض بأهلها فما بقى فهو لأولى رجل ذكر.)";
        }
        return new InheritanceShareDto(heirType,shareType,fixedShare,taaasibRule,reason);
    }
}
