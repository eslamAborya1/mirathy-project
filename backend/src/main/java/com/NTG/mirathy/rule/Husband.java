package com.NTG.mirathy.rule;


import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class Husband implements InheritanceRule {

    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.HUSBAND);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.HUSBAND;
        FixedShare share;

        TaaasibRule taaasibRule = null;
        String reason;

        if (c.hasDescendant()) {

            share = FixedShare.QUARTER;
            reason = "يرث الزوج من زوجته الربع إن كان لها فرع وارث سواء كان منه أو من غيره، والفرع الوارث هم: (( الأولاد بنون أو بنات، وأولاد الأبناء وإن نزلوا )) أما أولاد البنات فهم فروع غير وارثين. قال الله تعالى: (وَلَكُمْ نِصْفُ مَا تَرَكَ أَزْوَاجُكُمْ إِنْ لَمْ يَكُنْ لَهُنَّ وَلَدٌ فَإِنْ كَانَ لَهُنَّ وَلَدٌ فَلَكُمْ الرُّبُعُ مِمَّا تَرَكْنَ ..) .";
        } else {

            share = FixedShare.HALF;
            reason = "يرث الزوج من زوجته النصف إن لم يكن لها فرع وارث سواء كان منه أو من غيره، والفرع الوارث هم: (( الأولاد بنون أو بنات، وأولاد الأبناء وإن نزلوا )) أما أولاد البنات فهم فروع غير وارثين. قال الله تعالى: (وَلَكُمْ نِصْفُ مَا تَرَكَ أَزْوَاجُكُمْ إِنْ لَمْ يَكُنْ لَهُنَّ وَلَدٌ فَإِنْ كَانَ لَهُنَّ وَلَدٌ فَلَكُمْ الرُّبُعُ مِمَّا تَرَكْنَ ..) .";
        }

        return new InheritanceShareDto(heirType, ShareType.FIXED, share, taaasibRule, reason);
    }
}
