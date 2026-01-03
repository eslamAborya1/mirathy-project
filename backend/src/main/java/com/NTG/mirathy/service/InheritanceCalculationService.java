package com.NTG.mirathy.service;

import com.NTG.mirathy.DTOs.Fraction;
import com.NTG.mirathy.DTOs.InheritanceCalculationResult;
import com.NTG.mirathy.DTOs.InheritanceResult;
import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.DTOs.request.InheritanceCalculationRequest;
import com.NTG.mirathy.DTOs.response.InheritanceMemberResponse;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.exceptionHandler.InvalidInheritanceCaseException;
import com.NTG.mirathy.rule.InheritanceRule;
import com.NTG.mirathy.util.FractionUtils;
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

    public InheritanceCalculationResult calculateProblem(
            InheritanceCalculationRequest request
    ) {

        validateRequest(request);
        InheritanceCase inheritanceCase = new InheritanceCase(request.totalEstate(), request.debts(), request.will(), request.heirs());


        List<InheritanceShareDto> members = new ArrayList<>();


        for (InheritanceRule rule : rules) {
            if (rule.canApply(inheritanceCase)) {
                members.add(rule.calculate(inheritanceCase));
            }
        }

        return calculateIndividualFraction(members, request, request.totalEstate().doubleValue());
    }

    private InheritanceCalculationResult calculateIndividualFraction(List<InheritanceShareDto> members, InheritanceCalculationRequest request, Double money) {
        String title = arabicInheritanceTextService.generateText(request);
        String note = null;
        boolean taaasibRuleFound = false;
        BigDecimal sum = BigDecimal.ZERO;
        List<InheritanceResult> resultList = new ArrayList<>();
        List<Fraction> fractionList = new ArrayList<>();

        for (InheritanceShareDto member : members) {
            if (member.shareType() == ShareType.FIXED) {
                sum = sum.add(member.fixedShare().decimalValue());
                fractionList.add(new Fraction(member.fixedShare().getNumerator(), member.fixedShare().getDenominator()));
            }
            if (member.taaasibRule() != null) {
                taaasibRuleFound = true;
            }
        }


        System.out.println(FractionUtils.subtractSumFromOne(fractionList));


        ///////////////


        System.out.printf(fractionList.toString());

        System.out.println(fractionList);
        boolean elradFlag = false;
        if (sum.compareTo(BigDecimal.ONE) > 0) {
            fractionList = new ArrayList<>(applyAwlIfExists(fractionList));
            note = "المسألة بها عول وهو الزيادة فى عدد أسهم أصحاب الفروض على المقدار الاصلى للتركة و النقص فى مقادير أنصبائهم. ";
        } else if (sum.compareTo(BigDecimal.ONE) < 0 && !taaasibRuleFound) {
            elradFlag = true;
            System.out.println("الرد");
            note = "المسألة بها رد هو هو توزيع ما تبقّى من التركة على أصحاب الفروض (غير الزوجين) بنسبة فروضهم\n" +
                    "عند عدم وجود عاصب.";
            fractionList = new ArrayList<>(applyElrd(members));
        }
        int index = 0;
        for (InheritanceShareDto member : members) {
            if (member.shareType() == ShareType.FIXED) {
                boolean flag = member.taaasibRule() != null;


                Fraction fraction = new Fraction(member.fixedShare().getNumerator(),
                        member.fixedShare().getDenominator());

                String nasib = FractionUtils.text(fraction) + ((flag) ? (" + " + member.taaasibRule().getDescription()) : "")
                        + ((elradFlag && request.heirs().size() == 1) ? " و الباقى ردا" : "");

                int memberCount = request.heirs().get(member.heirType());


                if (flag) {
                    fraction = FractionUtils.SumTwoFractions(fractionList.get(index), FractionUtils.subtractSumFromOne(fractionList));
                    fractionList.set(index, fraction);
                }
                Fraction fractionNisbtElfard = new Fraction(fractionList.get(index).numerator(),
                        fractionList.get(index).denominator() * memberCount);


                String nisbtElfard = FractionUtils.text(fractionNisbtElfard);
                double individualAmount = (money * fractionNisbtElfard.numerator()) / fractionNisbtElfard.denominator();
                individualAmount = Math.round(individualAmount * 100.0) / 100.0;
                resultList.add(new InheritanceResult(
                        member.heirType().getArabicName(),
                        nasib,
                        memberCount,
                        nisbtElfard,
                        individualAmount,
                        member.reason()
                ));
                index++;

            } else if (member.shareType() == ShareType.TAASIB) {
                long count = members.stream().filter(m -> m.shareType() == ShareType.TAASIB).count();
                if (count == 1) {

                    Fraction fraction = FractionUtils.subtractSumFromOne(fractionList);

                    String nasib = member.taaasibRule().getDescription();

                    int memberCount = request.heirs().get(member.heirType());

                    Fraction fractionNisbtElfard = new Fraction(fraction.numerator(),
                            fraction.denominator() * memberCount);


                    String nisbtElfard = FractionUtils.text(fractionNisbtElfard);
                    double individualAmount = (money * fractionNisbtElfard.numerator()) / fractionNisbtElfard.denominator();
                    individualAmount = Math.round(individualAmount * 100.0) / 100.0;

                    resultList.add(new InheritanceResult(
                            member.heirType().getArabicName(),
                            nasib,
                            memberCount,
                            nisbtElfard,
                            individualAmount,
                            member.reason()));
                } else if (count == 2) {
                    boolean isMale = (member.heirType() == HeirType.SON) || (member.heirType() == HeirType.SON_OF_SON);
                    int elasham=1 ;
                    if ((member.heirType() == HeirType.SON)||(member.heirType() == HeirType.DAUGHTER)) {
                        elasham = request.heirs().get(HeirType.DAUGHTER) + 2 * request.heirs().get(HeirType.SON);
                    } else if ((member.heirType() == HeirType.SON_OF_SON)||(member.heirType() == HeirType.DAUGHTER_OF_SON)) {
                        elasham = request.heirs().get(HeirType.DAUGHTER_OF_SON) + 2 * request.heirs().get(HeirType.SON_OF_SON);
                    }


                    Fraction fraction = FractionUtils.subtractSumFromOne(fractionList);

                    String nasib = member.taaasibRule().getDescription();

                    int memberCount = request.heirs().get(member.heirType());

                    Fraction fractionNisbtElfard;

                    if (isMale) {
                        fractionNisbtElfard = new Fraction(fraction.numerator() * 2,
                                fraction.denominator() * elasham);
                    } else {
                        fractionNisbtElfard = new Fraction(fraction.numerator(),
                                fraction.denominator() * elasham);
                    }


                    String nisbtElfard = FractionUtils.text(fractionNisbtElfard);
                    double individualAmount = (money * fractionNisbtElfard.numerator()) / fractionNisbtElfard.denominator();


                    individualAmount = Math.round(individualAmount * 100.0) / 100.0;


                    System.out.println(individualAmount);
                    resultList.add(new InheritanceResult(
                            member.heirType().getArabicName(),
                            nasib,
                            memberCount,
                            nisbtElfard,
                            individualAmount,
                            member.reason()));
                }
            } else if (member.shareType() == ShareType.Mahgub) {
                int memberCount = request.heirs().get(member.heirType());
                resultList.add(new InheritanceResult(
                        member.heirType().getArabicName(),
                        "محجوب",
                        memberCount,
                        "0",
                        0.0,
                        member.reason()
                ));
            }
        }


        resultList.forEach(System.out::println);

        return new InheritanceCalculationResult(title, note, resultList);
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

    private static List<Fraction> applyElrd(List<InheritanceShareDto> members) {
        List<Fraction> fractionList = new ArrayList<>();
        Fraction sum = new Fraction(0, 1);
        Fraction f = new Fraction(1, 1);
        for (InheritanceShareDto member : members) {
            if (member.shareType() == ShareType.Mahgub) {
                continue;
            }

            Fraction fraction = new Fraction(member.fixedShare().getNumerator(), member.fixedShare().getDenominator());

            if ((member.heirType() == HeirType.WIFE) ||
                    (member.heirType() == HeirType.HUSBAND)) {
                f = FractionUtils.subtractSumFromOne(new ArrayList<>(List.of(fraction)));
            } else {
                sum = FractionUtils.SumTwoFractions(sum, fraction);
            }

        }

        for (InheritanceShareDto member : members) {
            if (member.shareType() == ShareType.Mahgub) {
                continue;
            }

            Fraction fraction = new Fraction(member.fixedShare().getNumerator(), member.fixedShare().getDenominator());

            if ((member.heirType() == HeirType.WIFE) ||
                    (member.heirType() == HeirType.HUSBAND)) {
                fractionList.add(fraction);
            } else {
                fraction = FractionUtils.divideTwoFractions(fraction, sum);
                fraction = new Fraction(fraction.numerator() * f.numerator(), fraction.denominator() * f.denominator());
                fractionList.add(fraction);
            }

        }
        return fractionList;
    }


    private static List<Fraction> applyAwlIfExists(List<Fraction> fractions) {

        if (fractions == null || fractions.isEmpty()) {
            return fractions;
        }

        // 1️⃣ حساب المقام الموحد (LCM)
        long unifiedDenominator = fractions.stream()
                .map(Fraction::denominator)
                .reduce(1L, InheritanceCalculationService::lcm);

        // 2️⃣ تحويل كل كسر إلى المقام الموحد وجمع البسوط
        long totalNumerators = fractions.stream()
                .mapToLong(f ->
                        f.numerator() * (unifiedDenominator / f.denominator())
                )
                .sum();

        // 3️⃣ التحقق هل حدث عول
        if (totalNumerators <= unifiedDenominator) {
            // ❌ لا يوجد عول
            return fractions;
        }

        // 4️⃣ تطبيق العول
        long awlDenominator = totalNumerators;

        return fractions.stream()
                .map(f -> new Fraction(
                        f.numerator() * (unifiedDenominator / f.denominator()),
                        awlDenominator
                ))
                .toList();
    }

    // ================== Helpers ==================

    private static long lcm(long a, long b) {
        return a * (b / gcd(a, b));
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

}
