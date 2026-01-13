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

        // 1️⃣ تقريب القيم الأساسية
        for (int i = 0; i < shares.size(); i++) {
            InheritanceShareDto s = shares.get(i);

            double perPerson = round(s.amountPerPerson(), 2);
            double total = round(perPerson * s.count(), 2);

            shares.set(i, s.withAmounts(perPerson, total));
        }

        // 2️⃣ مجموع الأنصبة بعد التقريب
        double totalAfterRound =
                shares.stream()
                        .mapToDouble(InheritanceShareDto::totalAmount)
                        .sum();

        // 3️⃣ حساب الفرق الحقيقي
        double diff = round(netEstate - totalAfterRound, 2);

        if (Math.abs(diff) < 0.01) return shares;

        // 4️⃣ تعديل أكبر نصيب (غير الزوج/الزوجة)
        InheritanceShareDto target =
                shares.stream()
                        .filter(s -> !s.heirType().isSpouse())
                        .max(Comparator.comparingDouble(InheritanceShareDto::totalAmount))
                        .orElse(null);

        if (target == null) return shares;

        int index = shares.indexOf(target);

        double newTotal = round(target.totalAmount() + diff, 2);
        double newPerPerson = round(newTotal / target.count(), 2);

        shares.set(index, target.withAmounts(newPerPerson, newTotal));

        return shares;
    }

    private static double round(double value, int places) {
        return BigDecimal
                .valueOf(value)
                .setScale(places, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
