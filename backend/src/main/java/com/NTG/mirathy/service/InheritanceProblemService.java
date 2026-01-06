package com.NTG.mirathy.service;

import com.NTG.mirathy.DTOs.response.FullInheritanceResponse;
import com.NTG.mirathy.DTOs.response.InheritanceMemberResponse;
import com.NTG.mirathy.DTOs.response.InheritanceProblemResponse;
import com.NTG.mirathy.Entity.InheritanceMember;
import com.NTG.mirathy.Entity.InheritanceProblem;
import com.NTG.mirathy.Entity.User;
import com.NTG.mirathy.Repository.InheritanceMemberRepo;
import com.NTG.mirathy.Repository.InheritanceProblemRepo;
import com.NTG.mirathy.util.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InheritanceProblemService {
    private final InheritanceProblemRepo inheritanceProblemRepo;
    private final UserService userService;
    private final SecurityUtil securityUtil;
    private final InheritanceMemberRepo  inheritanceMemberRepo;

    public List<InheritanceProblemResponse> findProblemAllByUser() {
        User user = getCurrentUser();
        Long userId = userService.getUserByEmail(user.getEmail()).getId();

        List<InheritanceProblem> problems = inheritanceProblemRepo.findAllByUserIdOrderByCreatedAtDesc(userId);
        return problems.stream().map(p -> new InheritanceProblemResponse(p.getId(), p.getTitle(), p.getCreatedAt(), p.getIsFavorite())).toList();
    }

    public List<InheritanceProblemResponse> findAllIsFavoriteProblem() {
        User user = getCurrentUser();
        Long userId = userService.getUserByEmail(user.getEmail()).getId();

        List<InheritanceProblem> problems = inheritanceProblemRepo.findAllByUserIdAndIsFavoriteTrueAndIsDeletedFalseOrderByCreatedAtDesc(userId);
        return problems.stream().map(p -> new InheritanceProblemResponse(p.getId(), p.getTitle(), p.getCreatedAt(), p.getIsFavorite())).toList();
    }

    @Transactional
    public InheritanceProblem saveInheritanceProblem(FullInheritanceResponse inheritanceProblem, User user) {
        InheritanceProblem problem = new InheritanceProblem();
        problem.setTitle(inheritanceProblem.title());
        problem.setTotalEstate(inheritanceProblem.totalEstate());
        problem.setNetEstate(inheritanceProblem.netEstate());
        problem.setUser(user);
        List<InheritanceMember> members = inheritanceProblem.shares().stream()
                .map(share -> {
                    InheritanceMember m = new InheritanceMember();
                    m.setMemberType(share.heirType());
                    m.setShareType(share.shareType());
                    m.setFixedShare(share.fixedShare());
                    m.setAmountPerPerson(share.amountPerPerson());
                    m.setTotalAmount(share.totalAmount());
                    m.setMemberCount(share.count());
                    m.setDescription(share.reason());
                    m.setProblem(problem);
                    return m;
                })
                .toList();
        problem.setMembers(members);
        return inheritanceProblemRepo.save(problem);
    }

    @Transactional
    public InheritanceProblemResponse ToggleFavoriteProblem(Long id) {
        InheritanceProblem inheritanceProblem = inheritanceProblemRepo.findById(id).orElse(null);
        if (inheritanceProblem != null) {
            inheritanceProblem.setIsFavorite(!inheritanceProblem.getIsFavorite());
        }
        assert inheritanceProblem != null;
        return new InheritanceProblemResponse(inheritanceProblem.getId(),inheritanceProblem.getTitle(), inheritanceProblem.getCreatedAt(), inheritanceProblem.getIsFavorite());
    }
    public List<InheritanceProblemResponse> findProblemAllIsFavorite() {
        List<InheritanceProblem> problems = inheritanceProblemRepo.findAllByUserIdAndIsFavoriteTrueAndIsDeletedFalseOrderByCreatedAtDesc(getCurrentUser().getId());
        return problems.stream().map(p -> new InheritanceProblemResponse(p.getId(), p.getTitle(), p.getCreatedAt(), p.getIsFavorite())).toList();
    }

    private User getCurrentUser() {
        return securityUtil.getCurrentUser();
    }


    public List<InheritanceMemberResponse> findInheritanceProblem(Long id) {
        List<InheritanceMember> members= inheritanceMemberRepo.findAllByProblemId(id);

        return members.stream().map(e->new InheritanceMemberResponse(e.getMemberType(),e.getShareType(),
                e.getFixedShare(),e.getAmountPerPerson(),e.getMemberCount(),e.getDescription())).toList();
    }
}
