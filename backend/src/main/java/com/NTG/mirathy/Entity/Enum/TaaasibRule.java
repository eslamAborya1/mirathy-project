package com.NTG.mirathy.Entity.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaaasibRule {
    // للذكر مثل حظ الأنثيين
    MALE_TWICE_FEMALE_ALL(
            "يرثون كامل التركة تعصبا للذكر مثل حظ الأنثيين"
    ),

    MALE_TWICE_FEMALE_REMAINDER(
            "يرثون باقي التركة تعصبا للذكر مثل حظ الأنثيين"
    ),

    // تعصيب بدون تفضيل
    REMAINDER_ONLY(
            "يرث باقي التركة تعصبا"
    ),

    ALL_ESTATE_ONLY(
            "يرث كامل التركة تعصبا"
    );
    private final String description;
}
