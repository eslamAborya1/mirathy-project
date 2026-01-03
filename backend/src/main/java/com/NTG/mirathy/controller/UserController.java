package com.NTG.mirathy.controller;

import com.NTG.mirathy.DTOs.InheritanceCalculationResult;
import com.NTG.mirathy.DTOs.request.InheritanceCalculationRequest;
import com.NTG.mirathy.DTOs.request.ProblemReportRequest;
import com.NTG.mirathy.DTOs.response.InheritanceMemberResponse;
import com.NTG.mirathy.DTOs.response.ProblemReportResponse;
import com.NTG.mirathy.service.InheritanceCalculationService;
import com.NTG.mirathy.service.ProblemReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {
    private final InheritanceCalculationService inheritanceCalculationService;
    private final ProblemReportService problemReportService;
    @GetMapping("calculate")
    public InheritanceCalculationResult calculate(@Valid @RequestBody InheritanceCalculationRequest request){
        return inheritanceCalculationService.calculateProblem(request);
    }
    @PostMapping("submit-report")
    public ResponseEntity<ProblemReportResponse> submitReport(
            @Valid @RequestBody ProblemReportRequest request
    ) {
        ProblemReportResponse response= problemReportService.reportProblem(request.problemId(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
