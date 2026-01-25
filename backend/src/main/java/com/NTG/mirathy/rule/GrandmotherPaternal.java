package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class GrandmotherPaternal implements InheritanceRule{
    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.GRANDMOTHER_PATERNAL);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.GRANDMOTHER_PATERNAL;
        String reason = null;
        FixedShare fixedShare = null;
        ShareType shareType = null;
        TaaasibRule taaasibRule = null;

        if (c.has(HeirType.FATHER)){
            shareType=ShareType.Mahgub;
            reason="كل من أدلت بوارث لا ترث فى وجوده فالأب يحجب أمه وأم أمه وأب الأب يحجب أمه وهكذا(إلا عند الحنابلة واختاره القانون السعودى فلا يحجب الأب والجد اى جدة أدلت بهم).";
        }else if (c.has(HeirType.MOTHER)){
            shareType=ShareType.Mahgub;
            reason="الأم تحجب جميع الجدات سواء من جهتها أو من جهة الأب لأن الجدات يرثن بصفة الأمومة المجازية ولا يمكن إعمال المجاز فى وجود الواقع.";
        }else if (c.has(HeirType.GRANDMOTHER_MATERNAL)){
            shareType=ShareType.FIXED;
            fixedShare=FixedShare.TWELVE;
            reason="فرض الجدة الصحيحة هو السدس .وتشتركن فيه الجدات الصحيحات المتحدات فى الدرجة ،أو جدة بعيدة من جهة الأم مع قريبة من جهة الأب (عند الشافعيه والمالكية واختار رأيهما القانون الجزائرى و المغربى والإماراتى). لما روى عن النبى ﷺ أنه أطعم السدس ثلاث جدات.والجدة الصحيحة هى أم الأم ومن علاها بمحض الإناث وأم الأب ومن علاها بمحض الذكور.والجدة الفاسدة هى من تدلى بذكر غير وارث مثل أم أب أم الميت فهى من ذوى الأرحام.";
        }else {
            shareType=ShareType.FIXED;
            fixedShare=FixedShare.SIXTH;
            reason="فرض الجدة الصحيحة هو السدس .وتشتركن فيه الجدات الصحيحات المتحدات فى الدرجة ،أو جدة بعيدة من جهة الأم مع قريبة من جهة الأب (عند الشافعيه والمالكية واختار رأيهما القانون الجزائرى و المغربى والإماراتى). لما روى عن النبى ﷺ أنه أطعم السدس ثلاث جدات.والجدة الصحيحة هى أم الأم ومن علاها بمحض الإناث وأم الأب ومن علاها بمحض الذكور.والجدة الفاسدة هى من تدلى بذكر غير وارث مثل أم أب أم الميت فهى من ذوى الأرحام.";
        }


        return new InheritanceShareDto(heirType, shareType, fixedShare, taaasibRule, reason);

    }
}
