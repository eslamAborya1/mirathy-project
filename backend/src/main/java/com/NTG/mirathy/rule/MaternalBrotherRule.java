package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class MaternalBrotherRule implements InheritanceRule {
    @Override
    public boolean canApply(InheritanceCase c) {
        if (!c.has(HeirType.MATERNAL_BROTHER)) return false;
        if (c.has(HeirType.FATHER) || c.has(HeirType.GRANDFATHER) ||
                c.has(HeirType.SON) || c.has(HeirType.SON_OF_SON) ||
                c.has(HeirType.DAUGHTER) || c.has(HeirType.DAUGHTER_OF_SON)) {
            return false;
        }
        return true;
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.MATERNAL_BROTHER;
        int count = c.count(heirType);
        int totalSiblings = count + c.count(HeirType.MATERNAL_SISTER);

        ShareType shareType = ShareType.FIXED;
        FixedShare fixedShare = null;
        String reason = "";

        if (totalSiblings == 1) {
            fixedShare = FixedShare.SIXTH;
            reason = "الأخ لأم يأخذ السدس منفرداً";
        } else {
            fixedShare = FixedShare.THIRD;
            reason = "الإخوة لأم يشتركون في الثلث";
        }

        return new InheritanceShareDto(
                heirType,
                count,
                null,
                null,
                shareType,
                fixedShare,
                reason
        );
    }
}