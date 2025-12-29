package com.NTG.mirathy.service;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.DTOs.request.InheritanceCalculationRequest;
import com.NTG.mirathy.DTOs.response.InheritanceMemberResponse;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.exceptionHandler.InvalidInheritanceCaseException;
import com.NTG.mirathy.rule.InheritanceRule;
import com.NTG.mirathy.util.InheritanceCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InheritanceCalculationService {

    private final List<InheritanceRule> rules;
    private final ArabicInheritanceTextService arabicInheritanceTextService;

    public InheritanceMemberResponse calculateProblem(
            InheritanceCalculationRequest request
    ) {

        validateRequest(request);
        InheritanceCase inheritanceCase = new InheritanceCase(request.totalEstate(), request.debts(), request.will(), request.heirs());
        String title = arabicInheritanceTextService.generateText(request);

        List<InheritanceShareDto> members = new ArrayList<>();

        for (InheritanceRule rule : rules) {
            if (rule.canApply(inheritanceCase)) {
                members.add(rule.calculate(inheritanceCase));
            }
        }

        members.forEach(System.out::println);
        calculateIndividualFraction(members);
        // مؤقتًا – بعدين هترجع list كاملة
        return new InheritanceMemberResponse(
                title,
                HeirType.FATHER,
                ShareType.FIXED,
                FixedShare.SIXTH,
                54.2,
                1,
                "res"
        );
    }
    private void calculateIndividualFraction(List<InheritanceShareDto> members) {
        boolean taaasibRuleFound = false;
        BigDecimal sum=BigDecimal.ZERO;
        for (InheritanceShareDto member : members) {
            if (member.shareType()==ShareType.FIXED) {
              sum= sum.add(member.fixedShare().decimalValue());
            }
            if (member.taaasibRule()!=null){
                taaasibRuleFound = true;
            }
        }
        if (sum.compareTo(BigDecimal.ONE) > 0) {
            //العول
            System.out.println("العول");
        } else if (sum.compareTo(BigDecimal.ONE)<0&&!taaasibRuleFound) {
            System.out.println("الرد");
            //الرد
        }else {
            System.out.println("we are here");
        }
    }


    private void validateRequest(InheritanceCalculationRequest request) {

        if (request == null) {
            throw new InvalidInheritanceCaseException("Request must not be null");
        }

        Map<HeirType, Integer> heirs = request.heirs();

        if (heirs == null || heirs.isEmpty()) {
            throw new InvalidInheritanceCaseException("Heirs must not be empty");
        }

        if (heirs.containsKey(HeirType.WIFE) && heirs.containsKey(HeirType.HUSBAND)) {
            throw new InvalidInheritanceCaseException("Heirs cannot have both Wife and Husband");
        }


        for (Map.Entry<HeirType, Integer> entry : heirs.entrySet()) {

            HeirType type = entry.getKey();
            Integer count = entry.getValue();

            if (type == null) {
                throw new InvalidInheritanceCaseException("Heir type must not be null");
            }

            if (count == null || count <= 0) {
                throw new InvalidInheritanceCaseException(
                        "Invalid count for heir: " + type
                );
            }

            Integer maxAllowed = type.getMAX_ALLOWED();

            if (maxAllowed != null && count > maxAllowed) {
                throw new InvalidInheritanceCaseException(
                        type + " must not be more than " + maxAllowed
                );
            }


        }

    }

}
