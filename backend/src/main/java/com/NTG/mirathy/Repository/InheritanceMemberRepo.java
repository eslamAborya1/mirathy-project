package com.NTG.mirathy.Repository;

import com.NTG.mirathy.Entity.InheritanceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InheritanceMemberRepo extends JpaRepository<InheritanceMember,Long> {
    List<InheritanceMember> findAllByProblemId(Long id);

}
