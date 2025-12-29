package com.NTG.mirathy.controller;

import com.NTG.mirathy.DTOs.response.ProblemReportResponse;
import com.NTG.mirathy.Entity.Enum.ProblemReportStauts;
import com.NTG.mirathy.Entity.ProblemReport;
import com.NTG.mirathy.service.ProblemReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ProblemReportService problemReportService;

    @GetMapping("/reports" )
    public ResponseEntity<List<ProblemReportResponse>> getAllProblemReports() {
        return ResponseEntity.ok(problemReportService.getAllProblemReports());
    }
    @PutMapping("/reports/{id}" )
    public ResponseEntity<Void> changeReportStatus(
            @PathVariable Long id,
            @RequestParam ProblemReportStauts status) {
        problemReportService.changeProblemReportStatus(id, status);
        return ResponseEntity.ok().build();
    }

}
