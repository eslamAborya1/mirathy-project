package com.NTG.mirathy.service;

import com.NTG.mirathy.DTOs.InheritanceCalculationResult;
import com.NTG.mirathy.DTOs.InheritanceShareDto;
import com.NTG.mirathy.Entity.InheritanceMember;
import com.NTG.mirathy.Entity.InheritanceProblem;
import com.NTG.mirathy.Entity.User;
import com.NTG.mirathy.Repository.InheritanceMemberRepo;
import com.NTG.mirathy.Repository.InheritanceProblemRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InheritanceProblemService {

//    private final InheritanceProblemRepo problemRepository;
//    private final InheritanceMemberRepo memberRepository;
//
//    @Transactional
//    public InheritanceProblem saveProblem(
//            InheritanceCalculationResult result,
//            User user
//    ) {
//        InheritanceProblem problem = new InheritanceProblem();
//        problem.setTitle(result.title());
//        problem.setNote(result.note());
//        problem.setUser(user);
//
//        problemRepository.save(problem);
//
//        List<InheritanceMember> members = result.shares().stream()
//                .map(dto -> mapToEntity(dto, problem))
//                .toList();
//
//        memberRepository.saveAll(members);
//
//        return problem;
//    }
//
//
//    private InheritanceMember mapToEntity(
//            InheritanceShareDto dto,
//            InheritanceProblem problem
//    ) {
//        return new InheritanceMember(
//                null,
//                dto.heirType(),
//                dto.fixedShare(),
//                dto.getShareDescription(),
//                dto.getReason(),
//                dto.getCount(),
//                dto.getIndividualFraction(),
//                dto.getShareValue(),
//                problem
//        );
//    }
}

