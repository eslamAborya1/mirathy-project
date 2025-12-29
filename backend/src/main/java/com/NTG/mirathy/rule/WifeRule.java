package com.NTG.mirathy.rule;

import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.Enum.FixedShare;
import com.NTG.mirathy.Entity.Enum.HeirType;
import com.NTG.mirathy.Entity.Enum.ShareType;
import com.NTG.mirathy.Entity.Enum.TaaasibRule;
import com.NTG.mirathy.Entity.InheritanceMember;
import com.NTG.mirathy.util.InheritanceCase;
import org.springframework.stereotype.Component;

@Component
public class WifeRule implements InheritanceRule {


    @Override
    public boolean canApply(InheritanceCase c) {
        return c.has(HeirType.WIFE);
    }

    @Override
    public InheritanceShareDto calculate(InheritanceCase c) {
        HeirType heirType = HeirType.WIFE;
        FixedShare share=null;
        TaaasibRule taaasibRule=null;
        String reason=null;


        if(c.hasDescendant()){
            share = FixedShare.EIGHTH;
            reason="ترث الزوجة من زوجها الثمن إن كان له فرع وارث سواء كان منها أو من غيرها، والفرع الوارث هم: (( الأولاد بنون أو بنات، وأولاد الأبناء وإن نزلوا )) أما أولاد البنات فهم فروع غير وارثين. قال الله تعالى: ( فَإِنْ كَانَ لَكُمْ وَلَدٌ فَلَهُنَّ الثُّمُنُ مِمَّا تَرَكْتُمْ ..) .";
        }
        else{
            share = FixedShare.QUARTER;
            reason="ترث الزوجة من زوجها الربع إن لم يكن له فرع وارث سواء كان منها أو من غيرها، والفرع الوارث هم: (( الأولاد بنون أو بنات، وأولاد الأبناء وإن نزلوا )) أما أولاد البنات فهم فروع غير وارثين. قال الله تعالى: (وَلَهُنَّ الرُّبُعُ مِمَّا تَرَكْتُمْ إِنْ لَمْ يَكُنْ لَكُمْ وَلَدٌ ..) .";
        }
        return new InheritanceShareDto(heirType, ShareType.FIXED, share,taaasibRule, reason);
    }
}
