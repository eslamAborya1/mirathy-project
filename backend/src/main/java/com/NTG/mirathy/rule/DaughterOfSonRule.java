package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class DaughterOfSonRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        // لا تنطبق إذا يوجد ابن صلب
        if (c.has(HeirType.SON)) return false;

        // تنطبق إذا هناك بنت الابن أو بنت صلبية واحدة أو أكثر
        return c.has(HeirType.DAUGHTER_OF_SON);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.DAUGHTER_OF_SON;
        int count = c.count(heirType);
        ShareType shareType = null;
        FixedShare fixedShare = null;
        String reason = "";

        int daughterCount = c.count(HeirType.DAUGHTER);

        // ===== حالة وجود بنت صلبية أو ابن الابن =====
        if (daughterCount >= 1 || c.has(HeirType.SON_OF_SON)) {
            shareType = ShareType.TAASIB; // تعصيب
            reason = "بنت الابن ترث بالتعصيب مع البنت الصلبية أو مع ابن الابن، للذكر مثل حظ الأنثيين";
        }
        // ===== حالة عدم وجود بنت صلبية ولا ابن الابن =====
        else {
            if (count == 1) {
                shareType = ShareType.FIXED;
                fixedShare = FixedShare.HALF; // نصف التركة
                reason = "بنت الابن ترث نصف التركة إذا كانت واحدة دون بنت صلب";
            } else if (count >= 2) {
                shareType = ShareType.FIXED;
                fixedShare = FixedShare.TWO_THIRDS; // ثلثا التركة
                reason = "لبنات الابن الثلثان لاشتراكهن في عدم وجود بنت صلب";
            }
        }

        if (shareType == null) return null;

        return new InheritanceShareDto(
                heirType, count, null, null, shareType, fixedShare, reason
        );
    }
}
