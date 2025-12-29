package com.NTG.mirathy.util;

import com.NTG.mirathy.Entity.Enum.HeirType;
import lombok.Getter;

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
        this.totalEstate = totalEstate;
        this.debts = debts;
        this.will = will;
        this.heirs = heirs == null ? new HashMap<>() : heirs;
    }


    public int count(HeirType type) {
        return heirs.getOrDefault(type, 0);
    }

    public boolean has(HeirType type) {
        return count(type) > 0;
    }

    public boolean hasChildren() {
        return has(HeirType.SON) || has(HeirType.DAUGHTER);
    }

    public boolean hasMaleChild() {
        return has(HeirType.SON)|| has(HeirType.SON_OF_SON);
    }
    public boolean hasFemaleChild() {
        return has(HeirType.DAUGHTER) || has(HeirType.DAUGHTER_OF_SON);
    }

    public boolean hasDescendant() {
        return has(HeirType.SON) || has(HeirType.DAUGHTER)
                || has(HeirType.DAUGHTER_OF_SON)
                || has(HeirType.SON_OF_SON);
    }

    public int totalNumberOfHeirs() {
        return heirs.size();
    }

    public BigDecimal getNetEstate() {
        return totalEstate.subtract(debts).subtract(will);
    }



}
