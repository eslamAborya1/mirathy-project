package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class MaternalSiblingsRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        return (c.has(HeirType.MATERNAL_BROTHER) || c.has(HeirType.MATERNAL_SISTER))
                && !c.hasAny(
                HeirType.FATHER,
                HeirType.GRANDFATHER,
                HeirType.SON,
                HeirType.SON_OF_SON,
                HeirType.DAUGHTER,
                HeirType.DAUGHTER_OF_SON
        );
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        int brothers = c.count(HeirType.MATERNAL_BROTHER);
        int sisters = c.count(HeirType.MATERNAL_SISTER);
        int total = brothers + sisters;

        FixedShare share =
                total == 1 ? FixedShare.SIXTH : FixedShare.THIRD;

        return new InheritanceShareDto(
                HeirType.MATERNAL_SIBLINGS, // نوع وهمي
                total,
                null,
                null,
                ShareType.FIXED,
                share,
                "الإخوة لأم يشتركون في " + (total == 1 ? "السدس" : "الثلث")
        );
    }
}
