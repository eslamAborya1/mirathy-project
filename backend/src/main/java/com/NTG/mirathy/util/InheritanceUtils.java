package com.NTG.mirathy.util;

import com.NTG.mirathy.DTOs.InheritanceShareDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

public class InheritanceUtils {

    public static List<InheritanceShareDto> roundShares(
            List<InheritanceShareDto> shares,
            double netEstate
    ) {
        if (shares == null || shares.isEmpty()) {
            return shares;
        }

        // ================== 1. التقريب الأساسي ==================
        List<InheritanceShareDto> roundedShares = shares.stream()
                .map(s -> roundSingleShare(s))
                .toList();

        // ================== 2. حساب المجموع بعد التقريب ==================
        BigDecimal totalAfterRound = BigDecimal.ZERO;
        for (InheritanceShareDto s : roundedShares) {
            totalAfterRound = totalAfterRound.add(
                    BigDecimal.valueOf(s.totalAmount())
            );
        }

        BigDecimal netEstateBD = BigDecimal.valueOf(netEstate);
        BigDecimal diff = netEstateBD.subtract(totalAfterRound);

        // ================== 3. إذا كان الفرق صغير جداً ==================
        if (diff.abs().compareTo(BigDecimal.valueOf(0.01)) < 0) {
            // أصلح أخطاء الدقة العشرية
            return fixPrecisionErrors(new java.util.ArrayList<>(roundedShares), netEstateBD);
        }

        // ================== 4. إذا كان الفرق كبير ==================
        if (diff.abs().compareTo(BigDecimal.valueOf(0.01)) >= 0) {
            return adjustShares(new java.util.ArrayList<>(roundedShares), diff, netEstateBD);
        }

        return new java.util.ArrayList<>(roundedShares);
    }

    // ================== تقريب نصيب واحد ==================
    private static InheritanceShareDto roundSingleShare(InheritanceShareDto share) {
        if (share == null) return share;

        BigDecimal total = BigDecimal.valueOf(share.totalAmount())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal perPerson = total.divide(
                BigDecimal.valueOf(share.count()),
                2,
                RoundingMode.HALF_UP
        );

        return share.withAmounts(
                perPerson.doubleValue(),
                total.doubleValue()
        );
    }

    // ================== إصلاح أخطاء الدقة العشرية ==================
    private static List<InheritanceShareDto> fixPrecisionErrors(
            List<InheritanceShareDto> shares,
            BigDecimal netEstateBD
    ) {
        // حساب المجموع الحالي
        BigDecimal currentTotal = BigDecimal.ZERO;
        for (InheritanceShareDto s : shares) {
            currentTotal = currentTotal.add(BigDecimal.valueOf(s.totalAmount()));
        }

        BigDecimal diff = netEstateBD.subtract(currentTotal);

        // إذا كان الفرق صفر أو شبه صفر، لا تفعل شيئاً
        if (diff.abs().compareTo(BigDecimal.valueOf(0.001)) < 0) {
            return shares;
        }

        // أضف الفرق البسيط لأكبر نصيب غير زوج/زوجة
        InheritanceShareDto largestNonSpouse = shares.stream()
                .filter(s -> !s.heirType().isSpouse())
                .max(Comparator.comparingDouble(InheritanceShareDto::totalAmount))
                .orElse(null);

        if (largestNonSpouse != null) {
            int index = shares.indexOf(largestNonSpouse);
            BigDecimal newTotal = BigDecimal.valueOf(largestNonSpouse.totalAmount())
                    .add(diff);

            double newPerPerson = newTotal.divide(
                    BigDecimal.valueOf(largestNonSpouse.count()),
                    2,
                    RoundingMode.HALF_UP
            ).doubleValue();

            shares.set(index, largestNonSpouse.withAmounts(
                    newPerPerson,
                    newTotal.doubleValue()
            ));
        }

        return shares;
    }

    // ================== تعديل الأنصبة للفرق الكبير ==================
    private static List<InheritanceShareDto> adjustShares(
            List<InheritanceShareDto> shares,
            BigDecimal diff,
            BigDecimal netEstateBD
    ) {
        // جمع أهل التعديل (غير الزوجين)
        BigDecimal eligibleTotal = BigDecimal.ZERO;
        java.util.Map<Integer, BigDecimal> eligibleMap = new java.util.HashMap<>();

        for (int i = 0; i < shares.size(); i++) {
            InheritanceShareDto s = shares.get(i);
            if (!s.heirType().isSpouse()) {
                BigDecimal amount = BigDecimal.valueOf(s.totalAmount());
                eligibleTotal = eligibleTotal.add(amount);
                eligibleMap.put(i, amount);
            }
        }

        // إذا لم يوجد أهل للتعديل (مثال: زوج فقط)
        if (eligibleTotal.compareTo(BigDecimal.ZERO) == 0) {
            // وزع الفرق بالتساوي
            BigDecimal shareDiff = diff.divide(
                    BigDecimal.valueOf(shares.size()),
                    2,
                    RoundingMode.HALF_UP
            );

            for (int i = 0; i < shares.size(); i++) {
                InheritanceShareDto s = shares.get(i);
                BigDecimal newTotal = BigDecimal.valueOf(s.totalAmount()).add(shareDiff);
                double newPerPerson = newTotal.divide(
                        BigDecimal.valueOf(s.count()),
                        2,
                        RoundingMode.HALF_UP
                ).doubleValue();

                shares.set(i, s.withAmounts(newPerPerson, newTotal.doubleValue()));
            }
            return shares;
        }

        // وزع الفرق بنسبة الأنصبة
        for (java.util.Map.Entry<Integer, BigDecimal> entry : eligibleMap.entrySet()) {
            int index = entry.getKey();
            BigDecimal amount = entry.getValue();
            BigDecimal ratio = amount.divide(eligibleTotal, 10, RoundingMode.HALF_UP);
            BigDecimal adjustment = diff.multiply(ratio);

            InheritanceShareDto s = shares.get(index);
            BigDecimal newTotal = amount.add(adjustment);
            double newPerPerson = newTotal.divide(
                    BigDecimal.valueOf(s.count()),
                    2,
                    RoundingMode.HALF_UP
            ).doubleValue();

            shares.set(index, s.withAmounts(newPerPerson, newTotal.doubleValue()));
        }

        return shares;
    }

    // ================== دالة التقريب العامة ==================
    private static double round(double value, int places) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        try {
            return BigDecimal.valueOf(value)
                    .setScale(places, RoundingMode.HALF_UP)
                    .doubleValue();
        } catch (Exception e) {
            return Math.round(value * Math.pow(10, places)) / Math.pow(10, places);
        }
    }
}