package com.NTG.mirathy.Repository;

import com.NTG.mirathy.Entity.InheritanceProblem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InheritanceProblemRepo extends JpaRepository<InheritanceProblem,Long> {
    Optional<InheritanceProblem> findById(int id);

    List<InheritanceProblem> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    List<InheritanceProblem>
    findAllByUserIdAndIsFavoriteTrueAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);
}