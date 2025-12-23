package com.NTG.mirathy.Entity.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HeirType {
    HUSBAND("زوج"),
    WIFE("زوجة"),
    FATHER("أب"),
    MOTHER("أم"),
    SON("ابن"),
    DAUGHTER("بنت");

    private final String arabicName;

}
