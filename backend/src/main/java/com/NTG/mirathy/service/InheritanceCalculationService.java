package com.NTG.mirathy.service;

import com.NTG.mirathy.DTOs.request.InheritanceCalculationRequest;
import com.NTG.mirathy.DTOs.response.InheritanceMemberResponse;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.InheritanceMember;
import com.NTG.mirathy.rule.InheritanceRule;
import com.NTG.mirathy.util.InheritanceCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InheritanceCalculationService {

    private final List<InheritanceRule> rules;


    public InheritanceMemberResponse calculateProblem(InheritanceCalculationRequest request) {

        InheritanceCase inheritanceCase = new InheritanceCase(request.totalEstate(), request.debts(), request.will(), request.heirs());

        List<InheritanceMember> members=new ArrayList<>();

        for (InheritanceRule rule : rules) {
            if (rule.canApply(inheritanceCase)){
              members.add(rule.calculate(inheritanceCase));
            }
        }

        return new InheritanceMemberResponse(HeirType.FATHER, ShareType.FIXED, FixedShare.SIXTH, 54.2, 5, "res");
    }
}
