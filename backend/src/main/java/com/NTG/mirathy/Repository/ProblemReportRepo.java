package com.NTG.mirathy.Repository;

import com.NTG.mirathy.Entity.ProblemReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemReportRepo extends JpaRepository<ProblemReport,Long> {
    List<ProblemReport> findByIsDeletedFalse();
}
