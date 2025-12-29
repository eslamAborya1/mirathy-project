package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.util.InheritanceCase;
import org.apache.tomcat.util.digester.Rule;
import org.springframework.stereotype.Component;

@Component
public class SonRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.SON);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.SON;
        String reason = null;
        FixedShare fixedShare = null;
        ShareType shareType = ShareType.TAASIB;
        TaaasibRule taaasibRule = null;


        if (c.totalNumberOfHeirs() == 1) {
            taaasibRule = TaaasibRule.ALL_ESTATE_ONLY;
            reason = "يرث الأبناء الذكور كامل التركة تعصبا عند الانفراد";

        } else if (c.totalNumberOfHeirs() >= 2 && c.has(HeirType.DAUGHTER)) {

            taaasibRule = (c.totalNumberOfHeirs() == 2) ? TaaasibRule.MALE_TWICE_FEMALE_ALL :
                    TaaasibRule.MALE_TWICE_FEMALE_REMAINDER;

            reason = "يرث الأبناء الذكور والإناث معا تعصيبا للذكر مثل حظ الأنثيين لقوله تعالى (يُوصِيكُمُ اللَّهُ فِي أَوْلادِكُمْ لِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ)";

        } else {
            taaasibRule = TaaasibRule.REMAINDER_ONLY;

            reason = "يرث الأبناء الذكور والإناث معا تعصيبا للذكر مثل حظ الأنثيين لقوله تعالى (يُوصِيكُمُ اللَّهُ فِي أَوْلادِكُمْ لِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ)";

        }
        return new InheritanceShareDto(heirType, shareType, fixedShare, taaasibRule, reason);
    }
}
