package com.NTG.mirathy.Entity.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HeirType {

    // ====== الأزواج ======
    HUSBAND("زوج", 1),
    WIFE("زوجة", 4),

    // ====== الأصول ======
    FATHER("أب", 1),
    MOTHER("أم", 1),
    GRANDFATHER("جد", 1),
    GRANDMOTHER_PATERNAL("جدة لأب", 1),
    GRANDMOTHER_MATERNAL("جدة لأم", 1),

    // ====== الفروع ======
    SON("ابن", null),
    DAUGHTER("بنت", null),
    SON_OF_SON("ابن الابن", null),
    DAUGHTER_OF_SON("بنت الابن", null),

    // ====== الإخوة الأشقاء ======
    FULL_BROTHER("أخ شقيق", null),
    FULL_SISTER("أخت شقيقة", null),

    // ====== الإخوة لأب ======
    PATERNAL_BROTHER("أخ لأب", null),
    PATERNAL_SISTER("أخت لأب", null),

    // ====== الإخوة لأم ======
    MATERNAL_BROTHER("أخ لأم", null),
    MATERNAL_SISTER("أخت لأم", null),
    MATERNAL_SIBLINGS("إخوة لأم", null),

    // ====== أبناء الإخوة ======
    SON_OF_FULL_BROTHER("ابن الأخ الشقيق", null),
    SON_OF_PATERNAL_BROTHER("ابن الأخ لأب", null),

    // ====== الأعمام ======
    FULL_UNCLE("عم شقيق", null),
    PATERNAL_UNCLE("عم لأب", null),

    // ====== أبناء الأعمام ======
    SON_OF_FULL_UNCLE("ابن العم الشقيق", null),
    SON_OF_PATERNAL_UNCLE("ابن العم لأب", null);

    private final String arabicName;
    private final Integer MAX_ALLOWED;

    /**
     * إرجاع وحدة العصبة لهذا النوع
     */
    public int getUnit() {
        return switch (this) {
            // الذكور العصبة = 2 وحدة
            case SON, SON_OF_SON,
                 FULL_BROTHER, PATERNAL_BROTHER,
                 SON_OF_FULL_BROTHER, SON_OF_PATERNAL_BROTHER,
                 FULL_UNCLE, PATERNAL_UNCLE,
                 SON_OF_FULL_UNCLE, SON_OF_PATERNAL_UNCLE
                    -> 2;

            // الإناث العصبة = 1 وحدة
            case DAUGHTER, DAUGHTER_OF_SON,
                 FULL_SISTER, PATERNAL_SISTER
                    -> 1;

            // العصبة بالنفس = 1 وحدة
            case FATHER, GRANDFATHER
                    -> 1;

            // غير ذلك = 0 (ليسوا عصبة)
            default -> 0;
        };
    }

    public boolean isSpouse() {
        return this == HUSBAND || this == WIFE;
    }

    /**
     * هل هذا الوارث عصبة؟
     */
    public boolean isAsaba() {
        return getUnit() > 0;
    }

    /**
     * ترتيب العصبة (الأقوى أولًا)
     */
    public int getAsabaRank() {
        return switch (this) {
            case SON -> 1;
            case SON_OF_SON -> 2;
            case FATHER -> 3;
            case GRANDFATHER -> 4;
            case FULL_BROTHER -> 5;
            case FULL_SISTER -> 6;
            case PATERNAL_BROTHER -> 7;
            case PATERNAL_SISTER -> 8;
            case SON_OF_FULL_BROTHER -> 9;
            case SON_OF_PATERNAL_BROTHER -> 10;
            case FULL_UNCLE -> 11;
            case PATERNAL_UNCLE -> 12;
            case SON_OF_FULL_UNCLE -> 13;
            case SON_OF_PATERNAL_UNCLE -> 14;
            default -> 100;
        };


    }
    public boolean isSinglePerson() {
        return this == FATHER || this == MOTHER || this == HUSBAND ||
                this==GRANDFATHER || this==GRANDMOTHER_MATERNAL || this==GRANDMOTHER_PATERNAL ||
                this.isSpouse();
    }

}