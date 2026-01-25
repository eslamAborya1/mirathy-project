package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class PaternalSister implements InheritanceRule {
    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.PATERNAL_SISTER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.PATERNAL_SISTER;
        ShareType shareType = null;
        FixedShare fixedShare = null;
        TaaasibRule taaasibRule = null;
        String reason = null;

        if (c.hasMaleChild() || c.has(HeirType.FATHER) ||
                c.has(HeirType.FULL_BROTHER) || (c.has(HeirType.FULL_SISTER) && (c.has(HeirType.DAUGHTER) || (c.has(HeirType.DAUGHTER_OF_SON)))) ||
                (c.count(HeirType.FULL_SISTER) >= 2 && !c.has(HeirType.PATERNAL_BROTHER))
        ) {
            shareType = ShareType.Mahgub;
            reason = "الإخوة والأخوات لأب محجوبون عند وجود الولد - مثل الإبن وإبن الإبن وإن نزل - أو وجود الوالد - الأب فقط عند الجمهور - ،بالجد عند ابي حنيفة أوالأخ الشقيق ، أو اجتماع الشقيقة مع فرع وارث مؤنث - مثل البنت وبنت الإبن- لأن الشقيقة تصير بالبنات عصبة مع الغيروتحجب الأخوة لأب رجالا ونساء.";
        } else if (c.has(HeirType.PATERNAL_BROTHER)) {
            shareType = ShareType.TAASIB;
            taaasibRule = (c.totalNumberOfHeirs() == 2) ? TaaasibRule.MALE_TWICE_FEMALE_ALL : TaaasibRule.MALE_TWICE_FEMALE_REMAINDER;
            reason = "الأخوة لأب رجالا ونساء يرثون معا بالتعصيب للذكر مثل حظ الأنثيين .قال تعالى ( وَإِن كَانُواْ إِخْوَةً رِّجَالاً وَنِسَاء فَلِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ) ، ويشترط لذلك أن تكون المسألة كلالة أى لا يكون هناك ولد - مثل الإبن الصلبى وابن الإبن وإن نزل - ولا والد-الأب فقط عند الجمهور - وإلا حجبوا بهم ، وألا يكون هناك إخوة اشقاء ذكور ، وألا تجتمع الشقيقة مع البنت أو بنت الإبن.";
        } else if ((c.has(HeirType.DAUGHTER) || c.has(HeirType.DAUGHTER_OF_SON))&&
                (!c.has(HeirType.FULL_SISTER))) {
            shareType = ShareType.TAASIB;
            taaasibRule = TaaasibRule.ASABA_WITH_OTHERS;
            reason = "الأخت لأب مثلها مثل الشقيقة ، فعند عدم الأخ لأب و الإخوة الأشقاء رجالا ونساء وفى وجود البنات - مثل البنت الصلبيه و بنت الإبن - تصير عصبة - مع الغير - فترث الباقى تعصيبا ولقول رسول الله (ص) (اجعلوا الأخوات مع البنات عصبة) , ويشترط لميراثها أن تكون المسألة كلاله أى لا يكون هناك ولد - مثل الإبن الصلبى وابن الإبن وإن نزل - ولا والد - الأب فقط عند الجمهور - وإلا حجبت بهم.";
        } else if (c.count(HeirType.FULL_SISTER)==1&&!c.has(HeirType.PATERNAL_BROTHER)) {
            shareType=ShareType.FIXED;
            fixedShare=FixedShare.SIXTH;
            reason="الأخت لأب - عند عدم الأخ لأب - مع الشقيقة مثلها مثل بنت الإبن مع البنت فترث السدس تكملة للثلثين ان كان هناك شقيقة واحدة ولا ترث شيئا إذا كان هناك أكثر من شقيقة ، ويشترط لذلك أن تكون المسألة كلاله أى ليس هناك ولد - مثل الإبن الصلبى وابن الإبن وإن نزل - أو والد - الأب فقط عند الجمهور - وإلا حجبت بهم ، وألا يكون هناك فرع وارث مؤنث مع الشقيقة لأن الشقيقة ستصير عصبة معهم -عصبة مع الغير ، وألا يكون هناك إخوة أشقاء ذكور.";
        } else if (c.count(HeirType.PATERNAL_SISTER)==1) {
            shareType=ShareType.FIXED;
            fixedShare=FixedShare.HALF;
            reason="الأخوات لأب ترث الواحدة منهن النصف ، وترث الأكثر من واحدة الثلثين وهذا بثلاثة شروط ، الاول أن تكون المسألة كلاله أى ليس هناك ولد - مثل الإبن الصلبى وابن الإبن وإن نزل - أو والد - الأب فقط عند الجمهور - وإلا حجبن بهم، والثانى عدم البنت وبنت الإبن وان نزلت ،والثالث عدم الإخوه والأخوات الأشقاء .قال تعالى ( يَسْتَفْتُونَكَ قُلِ اللَّهُ يُفْتِيكُمْ فِي الْكَلالَةِ إِنِ امْرُؤٌ هَلَكَ لَيْسَ لَهُ وَلَدٌ وَلَهُ أُخْتٌ فَلَهَا نِصْفُ مَا تَرَكَ وَهُوَ يَرِثُهَا إِن لَّمْ يَكُن لَّهَا وَلَدٌ فَإِن كَانَتَا اثْنَتَيْنِ فَلَهُمَا الثُّلُثَانِ).";
        }else {
            shareType=ShareType.FIXED;
            fixedShare=FixedShare.TWO_THIRDS;
            reason="الأخوات لأب ترث الواحدة منهن النصف ، وترث الأكثر من واحدة الثلثين وهذا بثلاثة شروط ، الاول أن تكون المسألة كلاله أى ليس هناك ولد - مثل الإبن الصلبى وابن الإبن وإن نزل - أو والد - الأب فقط عند الجمهور - وإلا حجبن بهم، والثانى عدم البنت وبنت الإبن وان نزلت ،والثالث عدم الإخوه والأخوات الأشقاء .قال تعالى ( يَسْتَفْتُونَكَ قُلِ اللَّهُ يُفْتِيكُمْ فِي الْكَلالَةِ إِنِ امْرُؤٌ هَلَكَ لَيْسَ لَهُ وَلَدٌ وَلَهُ أُخْتٌ فَلَهَا نِصْفُ مَا تَرَكَ وَهُوَ يَرِثُهَا إِن لَّمْ يَكُن لَّهَا وَلَدٌ فَإِن كَانَتَا اثْنَتَيْنِ فَلَهُمَا الثُّلُثَانِ).";
        }
        return new InheritanceShareDto(heirType, shareType, fixedShare, taaasibRule, reason);
    }
}
