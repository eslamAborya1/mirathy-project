package com.NTG.mirathy.controller;

import com.NTG.mirathy.DTOs.request.InheritanceCalculationRequest;
import com.NTG.mirathy.DTOs.request.ProblemReportRequest;
import com.NTG.mirathy.DTOs.response.FullInheritanceResponse;
import com.NTG.mirathy.DTOs.response.InheritanceMemberResponse;
import com.NTG.mirathy.DTOs.response.InheritanceProblemResponse;
import com.NTG.mirathy.DTOs.response.ProblemReportResponse;
import com.NTG.mirathy.service.InheritanceCalculationService;
import com.NTG.mirathy.service.InheritanceProblemService;
import com.NTG.mirathy.service.ProblemReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class UserController {
    private final InheritanceCalculationService inheritanceCalculationService;
    private final ProblemReportService problemReportService;
    private final InheritanceProblemService inheritanceProblemService;

    @PostMapping("calculate")
    public ResponseEntity<FullInheritanceResponse> calculate(
            @Valid @RequestBody InheritanceCalculationRequest request){
        FullInheritanceResponse response = inheritanceCalculationService.calculateProblem(request);

        return ResponseEntity.ok(response);
    }
    @PostMapping("submit-report")
    public ResponseEntity<ProblemReportResponse> submitReport(
            @Valid @RequestBody ProblemReportRequest request
    ) {
        ProblemReportResponse response= problemReportService.reportProblem(request.problemId(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/api/v1/auth/getAllProblem")
    public List<InheritanceProblemResponse> findProblemByUser() {
        return inheritanceProblemService.findProblemAllByUser();
    }

    @GetMapping("/api/v1/auth/getAllFavoriteProblem")
    public List<InheritanceProblemResponse> findFavoriteProblem() {
        return inheritanceProblemService.findProblemAllIsFavorite();
    }

    @PutMapping("/api/v1/auth/isFavorite/{id}")
    public ResponseEntity<InheritanceProblemResponse> addProblemToFavorite(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(inheritanceProblemService.ToggleFavoriteProblem(id));

    }
    @GetMapping("/api/v1/auth/problem/{id}")
    public ResponseEntity<List<InheritanceMemberResponse>> findInheritanceProblem(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(inheritanceProblemService.findInheritanceProblem(id));
    }
}
