package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class Daughter implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.DAUGHTER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.DAUGHTER;
        String reason=null;
        FixedShare fixedShare=null;
        ShareType shareType=null;
        TaaasibRule taaasibRule = null;
        if (c.has(HeirType.SON)) {
            shareType = ShareType.TAASIB;
            taaasibRule = (c.totalNumberOfHeirs() == 2) ? TaaasibRule.MALE_TWICE_FEMALE_ALL :
                    TaaasibRule.MALE_TWICE_FEMALE_REMAINDER;

            reason = "يرث الأبناء الذكور والإناث معا تعصيبا للذكر مثل حظ الأنثيين لقوله تعالى (يُوصِيكُمُ اللَّهُ فِي أَوْلادِكُمْ لِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ)";

        } else if (c.count(heirType) == 1) {
            shareType = ShareType.FIXED;
            fixedShare = FixedShare.HALF;
            reason = "ترث البنت الواحدة النصف إذا لم يكن لها أخ يعصبها. لقوله تعالى (يُوصِيكُمُ اللَّهُ فِي أَوْلادِكُمْ لِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ فَإِنْ كُنَّ نِسَاءً فَوْقَ اثْنَتَيْنِ فَلَهُنَّ ثُلُثَا مَا تَرَكَ وَإِنْ كَانَتْ وَاحِدَةً فَلَهَا النِّصْفُ)";
        } else {
            shareType = ShareType.FIXED;
            fixedShare = FixedShare.TWO_THIRDS;
            reason = "ترث البنتين فأكثر الثلثين إذا لم يكن لهن أخ يعصبهن. لقوله تعالى (يُوصِيكُمُ اللَّهُ فِي أَوْلادِكُمْ لِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ فَإِنْ كُنَّ نِسَاءً فَوْقَ اثْنَتَيْنِ فَلَهُنَّ ثُلُثَا مَا تَرَكَ وَإِنْ كَانَتْ وَاحِدَةً فَلَهَا النِّصْفُ) ولأنه ﷺ أعطى الثلثين لبنتى سعد بن الربيع، و الاثنتين لهما حكم فوق اثنتين لأن القرآن أيضا ذكر حُكم الأختين أن لهما الثلثين (فَإِن كَانَتَا اثْنَتَيْنِ فَلَهُمَا الثُّلُثَانِ مِمَّا تَرَكَ) فيتم حمل البنتين على الأختين ، وكذلك حمل الأكثر من أختين على قوله فى البنات (فَوْقَ اثْنَتَيْنِ).";
        }

        return new InheritanceShareDto(heirType, shareType, fixedShare, taaasibRule, reason);
    }


}
