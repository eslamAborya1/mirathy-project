package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.util.InheritanceCase;

public class Daughter implements InheritanceRule{

    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.DAUGHTER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        return null;
    }


}
