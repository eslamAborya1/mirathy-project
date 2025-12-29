package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class SonOfSonRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {

        // محجوب بوجود الابن
        if (c.has(HeirType.SON)) return false;

        return c.has(HeirType.SON_OF_SON);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        return new InheritanceShareDto(
                null,
                HeirType.SON_OF_SON,
                ShareType.TAASIB,
                null,
                "ابن الابن يرث تعصيبًا لعدم وجود الابن"
        );
    }
}
