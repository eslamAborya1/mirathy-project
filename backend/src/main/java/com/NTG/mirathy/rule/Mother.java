package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class Mother implements InheritanceRule {
    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.MOTHER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.MOTHER;
        FixedShare fixedShare = null;
        String reason = "";
        ShareType shareType = null;
        TaaasibRule taaasibRule = null;
        if (c.isElamaria()) {
            shareType=ShareType.FIXED;
            fixedShare=(c.has(HeirType.WIFE)?FixedShare.QUARTER:FixedShare.SIXTH);
            reason="ترث الأم ثلث الباقى بعد نصيب الزوج أو الزوجة. قضى عمر رضى الله عنه بذلك لأن الله تعالى قدّر للأب ضعفها إذا انفردا بكل التركة فيكون له ضعفها من البعض أيضا إذا انفردا ببعض التركه.";
        } else if (c.hasDescendant() || c.siblingCount(c) >= 2) {
            fixedShare = FixedShare.SIXTH;
            shareType = ShareType.FIXED;
            reason = "الأم ترث السدس ً" +
                    "عند وجود فرع وارث للمتوفى (ولد  أو ابن الابن) أو عند وجود جمع من الإخوة (اثنين أو أكثر)" +
                    "قوله تعالى: \"وَلِأَبَوَيْهِ لِكُلِّ وَاحِدٍ مِّنْهُمَا السُّدُسُ مِمَّا تَرَكَ إِن كَانَ لَهُ وَلَدٌ\" (النساء: 11)";
        } else {
            fixedShare = FixedShare.THIRD;
            shareType = ShareType.FIXED;
            reason = "الأم ترث الثلث ً" +
                    "عند عدم وجود فرع وارث للمتوفى (ولد أو ابن الابن) وعدم وجود جمع من الإخوة (أقل من اثنين)" +
                    "قوله تعالى: \"فَإِن لَّمْ يَكُن لَّهُ وَلَدٌ وَوَرِثَهُ أَبَوَاهُ فَلِأُمِّهِ الثُّلُثُ\" (النساء: 11)";

        }
        return new InheritanceShareDto(heirType, shareType, fixedShare, taaasibRule, reason);
    }

}
