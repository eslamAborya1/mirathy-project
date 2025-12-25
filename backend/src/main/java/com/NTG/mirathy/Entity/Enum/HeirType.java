package com.NTG.mirathy.Entity.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HeirType {
    HUSBAND("زوج",1),
    WIFE("زوجة",4),
    FATHER("أب",1),
    MOTHER("أم",1),
    SON("ابن",null),
    DAUGHTER("بنت",null);

    private final String arabicName;
    private final Integer MAX_ALLOWED;

}
