package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;
@Component
public class DaughterRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.DAUGHTER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.DAUGHTER;
        int count = c.count(heirType);
        ShareType shareType;
        FixedShare fixedShare = null;  // ⚠️ مهم: null عندما تكون مع ابن!
        String reason = "";

        if (c.has(HeirType.SON)) {
            shareType = ShareType.TAASIB;
            reason = "يرث الأبناء الذكور والإناث معا تعصيبا للذكر مثل حظ الأنثيين لقوله تعالى (يُوصِيكُمُ اللَّهُ فِي أَوْلادِكُمْ لِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ)";
            // ⚠️ fixedShare تبقى null! لأنها ليست فرضاً ثابتاً
        } else {
            shareType = ShareType.FIXED;
            if (count == 1) {
                fixedShare = FixedShare.HALF;
                reason = "ترث البنت الواحدة النصف إذا لم يكن لها أخ يعصبها...";
            } else {
                fixedShare = FixedShare.TWO_THIRDS;
                reason = "ترث البنتين فأكثر الثلثين...";
            }
        }

        return new InheritanceShareDto(
                heirType,
                count,
                null,
                null,
                shareType,
                fixedShare,  // ⚠️ قد تكون null
                reason
        );
    }
}