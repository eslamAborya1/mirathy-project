package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class FullSister implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.FULL_SISTER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.FULL_SISTER;
        String reason=null;
        FixedShare fixedShare=null;
        ShareType shareType=null;
        TaaasibRule taaasibRule = null;

      if (c.hasMaleChild()||c.has(HeirType.FATHER)) {
          shareType=ShareType.Mahgub;
          reason="لا يرث الإخوه والأخوات الأشقاء عند وجود الولد - مثل الإبن وإبن الإبن وإن نزل - أو وجود الوالد - الأب فقط عند الجمهور";
      } else if (c.has(HeirType.FULL_BROTHER)) {
          shareType=ShareType.TAASIB;
          taaasibRule=(c.totalNumberOfHeirs()==2)?TaaasibRule.MALE_TWICE_FEMALE_ALL:TaaasibRule.MALE_TWICE_FEMALE_REMAINDER;
          reason="لأخوة الأشقاء رجالاً ونساء يرثون معاً بالتعصيب للذكر مثل حظ الأنثيين .قال تعالى ( وَإِن كَانُواْ إِخْوَةً رِّجَالاً وَنِسَاء فَلِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ) ، ويشترط لذلك أن تكون المسألة كلالة أى لا يكون هناك ولد - مثل الإبن الصلبى وابن الإبن وإن نزل - ولا والد - الأب فقط عند الجمهور - وإلا حجبوا بهم.";
      } else if (c.has(HeirType.DAUGHTER)||c.has(HeirType.DAUGHTER_OF_SON)) {
         shareType=ShareType.TAASIB;
         taaasibRule=TaaasibRule.ASABA_WITH_OTHERS;
         reason="الأخت الشقيقة - عند عدم الأخ الشقيق - وفى وجود البنات - مثل البنت الصلبيه و بنت الإبن - تصير عصبة - مع الغير - فترث الباقى تعصيبا ولقول رسول الله (ص) (اجعلوا الأخوات مع البنات عصبة) , لقول بن مسعود - رضى الله عنه - فى مسألة بنت وبنت ابن واخت شقيقة (أقضى فيها بقضاء رسول الله للبنت النصف،ولبنت الإبن السدس تكملة للثلثين ،وللأخت الشقية الباقى ) ،  ويشترط لميراثها أن تكون المسألة كلاله أى لا يكون هناك ولد - مثل الإبن الصلبى وابن الإبن وإن نزل - ولا والد - الأب فقط عند الجمهور - وإلا حجبت بهم.";
      } else if (c.count(HeirType.FULL_SISTER)==1) {
          shareType=ShareType.FIXED;
          fixedShare=FixedShare.HALF;
          reason="الأخت الشقيقة - عند عدم الأخ الشقيق - مثلها مثل البنت - إذا لم يكن هناك بنات صلبيات أو بنات ابن - فتأخذ الشقيقة النصف ان كانت واحده والثلثان ان كانتا اثنتين أو أكثر .قال تعالى ( يَسْتَفْتُونَكَ قُلِ اللَّهُ يُفْتِيكُمْ فِي الْكَلالَةِ إِنِ امْرُؤٌ هَلَكَ لَيْسَ لَهُ وَلَدٌ وَلَهُ أُخْتٌ فَلَهَا نِصْفُ مَا تَرَكَ وَهُوَ يَرِثُهَا إِن لَّمْ يَكُن لَّهَا وَلَدٌ فَإِن كَانَتَا اثْنَتَيْنِ فَلَهُمَا الثُّلُثَانِ) ويشترط لذلك أن تكون المسألة كلالة أى لا يكون هناك ولد - مثل الإبن الصلبى وابن الإبن وإن نزل - ولا والد - الأب فقط عند الجمهور - وإلا حجبت بهم.";
      }else {
          shareType=ShareType.FIXED;
          fixedShare=FixedShare.TWO_THIRDS;
          reason="الأخت الشقيقة - عند عدم الأخ الشقيق - مثلها مثل البنت - إذا لم يكن هناك بنات صلبيات أو بنات ابن - فتأخذ الشقيقة النصف ان كانت واحده والثلثان ان كانتا اثنتين أو أكثر .قال تعالى ( يَسْتَفْتُونَكَ قُلِ اللَّهُ يُفْتِيكُمْ فِي الْكَلالَةِ إِنِ امْرُؤٌ هَلَكَ لَيْسَ لَهُ وَلَدٌ وَلَهُ أُخْتٌ فَلَهَا نِصْفُ مَا تَرَكَ وَهُوَ يَرِثُهَا إِن لَّمْ يَكُن لَّهَا وَلَدٌ فَإِن كَانَتَا اثْنَتَيْنِ فَلَهُمَا الثُّلُثَانِ) ويشترط لذلك أن تكون المسألة كلالة أى لا يكون هناك ولد - مثل الإبن الصلبى وابن الإبن وإن نزل - ولا والد - الأب فقط عند الجمهور - وإلا حجبت بهم.";
      }
        return new InheritanceShareDto(heirType, shareType, fixedShare, taaasibRule, reason);
    }


}
