package com.NTG.mirathy.util;

import com.NTG.mirathy.Entity.Enum.HeirType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class InheritanceCase {

    private final BigDecimal totalEstate;
    private final BigDecimal debts;
    private final BigDecimal will;
    private final Map<HeirType, Integer> heirs;

    public InheritanceCase(
            BigDecimal totalEstate,
            BigDecimal debts,
            BigDecimal will,
            Map<HeirType, Integer> heirs
    ) {
        this.totalEstate = totalEstate != null ? totalEstate : BigDecimal.ZERO;
        this.debts = debts != null ? debts : BigDecimal.ZERO;
        this.will = will != null ? will : BigDecimal.ZERO;
        this.heirs = heirs != null ? new HashMap<>(heirs) : new HashMap<>();
    }

    /* ===================== Basic ===================== */

    public int count(HeirType type) {
        return heirs.getOrDefault(type, 0);
    }

    public boolean has(HeirType type) {
        return count(type) > 0;
    }

    public boolean hasAny(HeirType... types) {
        for (HeirType type : types) {
            if (has(type)) {
                return true;
            }
        }
        return false;
    }

    /* ===================== Descendants ===================== */

    public boolean hasChildren() {
        return has(HeirType.SON) || has(HeirType.DAUGHTER);
    }

    public boolean hasMaleChild() {
        return has(HeirType.SON) || has(HeirType.SON_OF_SON);
    }

    public boolean hasFemaleChild() {
        return (has(HeirType.DAUGHTER) || has(HeirType.DAUGHTER_OF_SON))
               && !hasMaleChild();
    }

    public boolean hasDescendant() {
        return hasAny(
                HeirType.SON,
                HeirType.DAUGHTER,
                HeirType.SON_OF_SON,
                HeirType.DAUGHTER_OF_SON
        );
    }

    /* ===================== Spouse ===================== */

    public boolean hasSpouse() {
        return hasAny(HeirType.HUSBAND, HeirType.WIFE);
    }

    /* ===================== Counts ===================== */

    public int countMaleChildren() {
        return count(HeirType.SON);
    }

    public int countFemaleChildren() {
        return count(HeirType.DAUGHTER);
    }

    public int countTotalChildren() {
        return countMaleChildren() + countFemaleChildren();
    }

    public int countSiblings() {
        return count(HeirType.FULL_BROTHER)
                + count(HeirType.FULL_SISTER)
                + count(HeirType.PATERNAL_BROTHER)
                + count(HeirType.PATERNAL_SISTER)
                + count(HeirType.MATERNAL_BROTHER)
                + count(HeirType.MATERNAL_SISTER);
    }

    public boolean hasSiblings() {
        return countSiblings() > 0;
    }

    /* ===================== Estate ===================== */

    public BigDecimal getNetEstate() {
        return totalEstate
                .subtract(debts)
                .subtract(will)
                .max(BigDecimal.ZERO);
    }

    public Map<HeirType, Integer> getHeirs() {
        return new HashMap<>(heirs);
    }

    public int mapSize() {
        return heirs.size();
    }

    /* ===================== Helper Methods ===================== */

    /**
     * إجمالي وحدات العصبة في المسألة
     */
    public int getTotalAsabaUnits() {
        int total = 0;
        for (Map.Entry<HeirType, Integer> entry : heirs.entrySet()) {
            HeirType type = entry.getKey();
            int count = entry.getValue();
            if (type.isAsaba()) {
                total += count * type.getUnit();
            }
        }
        return total;
    }

    /**
     * هل يوجد عصبة في المسألة؟
     */
    public boolean hasAsaba() {
        return heirs.keySet().stream().anyMatch(HeirType::isAsaba);
    }
}