package com.NTG.mirathy.rule;

import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.InheritanceMember;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class FatherRule implements InheritanceRule{


    @Override
    public boolean canApply(InheritanceCase c) {
        return  c.has(HeirType.FATHER);
    }

    @Override
    public InheritanceMember calculate(InheritanceCase inheritanceCase) {
        System.out.println("Father rule calculated");
        return null;
    }
}
