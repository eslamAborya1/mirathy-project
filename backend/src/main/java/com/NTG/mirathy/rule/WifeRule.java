package com.NTG.mirathy.rule;

import com.NTG.mirathy.Entity.InheritanceMember;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class WifeRule implements InheritanceRule {


    @Override
    public boolean canApply(InheritanceCase c) {
        return false;
    }

    @Override
    public InheritanceMember calculate(InheritanceCase c) {
        return null;
    }
}
