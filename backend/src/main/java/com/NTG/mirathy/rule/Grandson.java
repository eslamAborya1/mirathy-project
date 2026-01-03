package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class Grandson implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase inheritanceCase) {
        return inheritanceCase.has(HeirType.SON_OF_SON);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase inheritanceCase) {
        HeirType heirType = HeirType.SON_OF_SON;
        String reason = null;
        FixedShare fixedShare = null;
        ShareType shareType = null;
        TaaasibRule taaasibRule = null;

        if (inheritanceCase.has(HeirType.SON)) {
            shareType = ShareType.Mahgub;
            reason = "من أدلى الى الميت بواسطة حجبته تلك الواسطة عند وجودها .ولذلك ابن الإبن محجوب عند وجود الابن أو ابن ابن أعلى منه فى الدرجة.";
        } else if (inheritanceCase.totalNumberOfHeirs() == 1) {
            shareType=ShareType.TAASIB;
            taaasibRule = TaaasibRule.ALL_ESTATE_ONLY;
            reason = "يرث ابناء الأبناء الذكور كامل التركة تعصبا عند الانفراد";
        } else if (inheritanceCase.totalNumberOfHeirs() >= 2 && inheritanceCase.has(HeirType.DAUGHTER_OF_SON)) {
            shareType=ShareType.TAASIB;
            taaasibRule = (inheritanceCase.totalNumberOfHeirs() == 2) ? TaaasibRule.MALE_TWICE_FEMALE_ALL :
                    TaaasibRule.MALE_TWICE_FEMALE_REMAINDER;

            reason = "يرث ابناء الابن و بنات الأبن معا للذكور والإناث معا تعصيبا للذكر مثل حظ الأنثيين لقوله تعالى (يُوصِيكُمُ اللَّهُ فِي أَوْلادِكُمْ لِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ)";

        } else {
            taaasibRule = TaaasibRule.REMAINDER_ONLY;
            shareType=ShareType.TAASIB;
            reason = "يرث ابناء الابن الذكور باقى التركة تعصبا";

        }

        return new InheritanceShareDto(heirType, shareType, fixedShare, taaasibRule, reason);
    }
}
