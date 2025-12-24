package com.NTG.mirathy.rule;

import com.NTG.mirathy.Entity.InheritanceMember;
import com.NTG.mirathy.util.InheritanceCase;

public interface   InheritanceRule {

      boolean canApply(InheritanceCase c);

      InheritanceMember calculate(InheritanceCase c);

}
