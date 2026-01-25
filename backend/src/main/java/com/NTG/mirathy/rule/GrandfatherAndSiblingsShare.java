package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.Fraction;
import com.NTG.mirathy.DTOs.InheritanceResult;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.util.FractionUtils;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GrandfatherAndSiblingsShare {

    public boolean canApplyGrandfatherAndSiblingsShare(InheritanceCase inheritanceCase) {
        return !inheritanceCase.hasMaleChild()
                && !inheritanceCase.has(HeirType.FATHER)
                && inheritanceCase.has(HeirType.GRANDFATHER)
                && inheritanceCase.fullSiblingAndPaternalSibling() > 0;
    }


    public List<InheritanceResult> applyIfSharingBetterForGrandfather(List<InheritanceResult> resultList,double money) {
        Fraction fractionSum = new Fraction(0, 1);
        Fraction grandfatherFraction = new Fraction(0, 1);
        int countAlsahm = 0;

        String reason="اختار القانون المصرى منهج الإمام على فى توريث الجد والاخوة فيرث معهم كأخ مثلهم ان كانوا اخوة ذكورا فقط أو ذكورا وإناثا (منهج علي) او كانوا أخوة إناثا عصبة مع البنات (خالف فيها القانون علياً وأخذها من زيد بن ثابت)، أما ان كان الإخوة إناثا فقط ولم يعصبن بالبنات فله باقى التركة ، والشرط الأساسي ان لايكون ميراثه معهم كأخ أو أخذه الباقى ينقصة عن السدس وإلا أخذ السدس\n\n وفى هذه المسألة يشارك الجد الأخوة و يرث كأخ لأنه فى هذه المسألة المشاركة تعطيه اكثر من السدس";


        for (InheritanceResult result : resultList) {
            if (result.nasib().equals("محجوب")) continue;

            if (result.heirType().equals(HeirType.GRANDFATHER.getArabicName()) ||
                    isMaleSibling(result) ||
                    isFemaleSibling(result)
            ) {



                Fraction fraction = FractionUtils.covertFractionTextToText(result.nisbtElfard());

                if (isGrandfather(result)) {
                    grandfatherFraction = fraction;
                }

                if (isFemaleSibling(result)) {
                    countAlsahm += (result.countMembers());
                } else {
                    countAlsahm += result.countMembers()*2;
                }



                fraction = new Fraction(fraction.numerator() * result.countMembers(),
                        fraction.denominator());


                fractionSum = FractionUtils.SumTwoFractions(fractionSum, fraction);

            }
        }

        Fraction alsaham =FractionUtils.divideTwoFractions(fractionSum,new Fraction(countAlsahm,1));

        Fraction maleFraction=new Fraction(alsaham.numerator()*2,alsaham.denominator());
        Fraction femaleFraction=new Fraction(alsaham.numerator(),alsaham.denominator());


        if (FractionUtils.compareFractions(grandfatherFraction,maleFraction) >= 0) {
            return resultList;
        }

        List<InheritanceResult> newResultList = new ArrayList<>();

        for (InheritanceResult result : resultList) {

            if (isGrandfather(result)) {
                InheritanceResult inheritanceResult=new InheritanceResult(
                        result.heirType(),
                        "يشارك الجد الاخوة ويرث معهم كأخ",
                        result.countMembers(),
                        FractionUtils.text(maleFraction),
                        Math.round((money*maleFraction.numerator()/maleFraction.denominator())*100.0)/100.0,
                        reason
                );

                newResultList.add(inheritanceResult);

            }else if (isFemaleSibling(result)) {
                InheritanceResult inheritanceResult=new InheritanceResult(
                        result.heirType(),
                        "يشارك الجد والأخوات",
                        result.countMembers(),
                        FractionUtils.text(femaleFraction),
                        Math.round((money*femaleFraction.numerator()/maleFraction.denominator())*100.0)/100.0,
                        reason
                );

                newResultList.add(inheritanceResult);
            }else if (isMaleSibling(result)) {

                InheritanceResult inheritanceResult=new InheritanceResult(
                        result.heirType(),
                        "يشارك الجد الاخوة والأخوات",
                        result.countMembers(),
                        FractionUtils.text(maleFraction),
                        Math.round((money*maleFraction.numerator()/maleFraction.denominator())*100.0)/100.0,
                        reason
                );

                newResultList.add(inheritanceResult);

            }else {
                newResultList.add(result);
            }
        }
        return newResultList;
    }


    private boolean isMaleSibling(InheritanceResult result) {
        return result.heirType().equals(HeirType.FULL_BROTHER.getArabicName()) ||
                result.heirType().equals(HeirType.PATERNAL_BROTHER.getArabicName());
    }

    private boolean isFemaleSibling(InheritanceResult result) {
        return result.heirType().equals(HeirType.FULL_SISTER.getArabicName()) ||
                result.heirType().equals(HeirType.PATERNAL_SISTER.getArabicName());
    }

    private boolean isGrandfather(InheritanceResult result) {
        return result.heirType().equals(HeirType.GRANDFATHER.getArabicName());
    }

}
