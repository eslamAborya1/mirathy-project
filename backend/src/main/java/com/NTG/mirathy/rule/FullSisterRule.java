package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.*;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class FullSisterRule implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        if (c.has(HeirType.FATHER) || c.has(HeirType.GRANDFATHER)) return false;
        if (c.has(HeirType.SON) || c.has(HeirType.SON_OF_SON)) return false;
        return c.has(HeirType.FULL_SISTER);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {

        if (c.has(HeirType.FULL_BROTHER)) {
            return new InheritanceShareDto(
                    HeirType.FULL_SISTER,
                    c.count(HeirType.FULL_SISTER),
                    null,
                    null,
                    ShareType.TAASIB,
                    null,
                    "الأخت الشقيقة ترث تعصيبًا مع الأخ الشقيق  للذكر مثل حظ الأنثيين .قال تعالى ( وَإِن كَانُواْ إِخْوَةً رِّجَالاً وَنِسَاء فَلِلذَّكَرِ مِثْلُ حَظِّ الأُنثَيَيْنِ) ، ويشترط لذلك أن تكون المسألة كلالة أى لا يكون هناك ولد - مثل الإبن الصلبى وابن الإبن وإن نزل - ولا والد - الأب فقط عند الجمهور - وإلا حجبوا بهم"
            );
        }

        FixedShare fixedShare =
                c.count(HeirType.FULL_SISTER) == 1 ? FixedShare.HALF : FixedShare.TWO_THIRDS;

        return new InheritanceShareDto(
                HeirType.FULL_SISTER,
                c.count(HeirType.FULL_SISTER),
                null,
                null,
                ShareType.FIXED,
                fixedShare,
                "للأخت الشقيقة فرضها عند الكلالة"
        );
    }
}
