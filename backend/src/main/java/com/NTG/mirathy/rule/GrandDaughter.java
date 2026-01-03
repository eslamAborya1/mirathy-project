package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class GrandDaughter implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase inheritanceCase) {
        return inheritanceCase.has(HeirType.DAUGHTER_OF_SON);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase inheritanceCase) {
        HeirType heirType = HeirType.DAUGHTER_OF_SON;
        String reason = null;
        FixedShare fixedShare = null;
        ShareType shareType = null;
        TaaasibRule taaasibRule = null;
        if (inheritanceCase.has(HeirType.SON)) {
            shareType = ShareType.Mahgub;
            reason = "لا ترث بنات الأبناء عند وجود الإبن الصلبى أو ابن ابن أعلى منهن فى الدرجه";
        } else if (inheritanceCase.count(HeirType.DAUGHTER) >= 2 &&
                inheritanceCase.count(HeirType.SON_OF_SON) == 0) {
            shareType = ShareType.Mahgub;
            reason = "لا ترث بنت الإبن إذا كان هناك أكثر من بنت صلبيه لأنهن سيستغرقن الثلثين فلا ترث بنت الإبن فى هذه الحاله الا اذا كان معها ابن ابن فى درجتها أو أدنى منها درجة فيعصبها";
        } else if (inheritanceCase.count(HeirType.DAUGHTER) == 1 &&
                inheritanceCase.count(HeirType.SON_OF_SON) == 0) {
            shareType = ShareType.FIXED;
            fixedShare = FixedShare.SIXTH;
            reason = "بنت الإبن مع البنت الصلبيه الواحده فى عدم الإبن الصلبى وابن الإبن الذى فى درجتها - يعصبها - وابن الإبن الأعلى منها درجة - يحجبها - ترث السدس تكملة للثلثين تنفرد به الواحده وتشترك فيه الأكثر من واحده لقول ابن مسعود - رضى الله عنه - فى مسألة بنت وبنت ابن وأخت شقيقة (أقضى فيها بقضاء رسول الله للبنت النصف،ولبنت الإبن السدس تكملة للثلثين ،وللأخت الشقية الباقى )";
        } else if (inheritanceCase.count(HeirType.SON_OF_SON) >= 1) {
            taaasibRule = TaaasibRule.MALE_TWICE_FEMALE_REMAINDER;
            shareType = ShareType.TAASIB;
            reason = "يرث أبناء الأبناء الذكور والإناث معا تعصيبا للذكر مثل حظ الأنثيين لقوله تعالى (يُوصِيكُمُ اللَّهُ فِي أَوْلادِكُمْ لِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ) ،ولا فرق بين أن يكون ابن الإبن اخ لبنت الإبن أوابن عمها.";
        } else if (inheritanceCase.count(HeirType.DAUGHTER_OF_SON) == 1) {
            fixedShare = FixedShare.HALF;
            shareType = ShareType.FIXED;
            reason = "بنات الإبن - مثل بنت الإبن وبنت ابن الإبن - مثلهن مثل البنت بشرط عدم وجود بنت صلبيه أوابن صلبى أو ابن ابن أعلى منهن فيحجبهن. فترث الواحدة من بنات الابن النصف إذا لم يكن هناك ابن ابن فى درجتها يعصبها وترث الأكثر من واحدة الثلثين . قال تعالى (يُوصِيكُمُ اللَّهُ فِي أَوْلادِكُمْ لِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ فَإِنْ كُنَّ نِسَاءً فَوْقَ اثْنَتَيْنِ فَلَهُنَّ ثُلُثَا مَا تَرَكَ وَإِنْ كَانَتْ وَاحِدَةً فَلَهَا النِّصْفُ)";
        } else   {
            fixedShare = FixedShare.TWO_THIRDS;
            shareType = ShareType.FIXED;
            reason = "بنات الإبن - مثل بنت الإبن وبنت ابن الإبن - مثلهن مثل البنت بشرط عدم وجود بنت صلبيه أوابن صلبى أو ابن ابن أعلى منهن فيحجبهن. فترث الواحدة من بنات الابن النصف إذا لم يكن هناك ابن ابن فى درجتها يعصبها وترث الأكثر من واحدة الثلثين . قال تعالى (يُوصِيكُمُ اللَّهُ فِي أَوْلادِكُمْ لِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ فَإِنْ كُنَّ نِسَاءً فَوْقَ اثْنَتَيْنِ فَلَهُنَّ ثُلُثَا مَا تَرَكَ وَإِنْ كَانَتْ وَاحِدَةً فَلَهَا النِّصْفُ)";
        }

        return new InheritanceShareDto(heirType, shareType, fixedShare, taaasibRule, reason);
    }
}
