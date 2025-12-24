package com.NTG.mirathy.controller;

import com.NTG.mirathy.DTOs.request.InheritanceCalculationRequest;
import com.NTG.mirathy.DTOs.response.InheritanceMemberResponse;
import com.NTG.mirathy.service.InheritanceCalculationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class InheritanceCalculationController {

    private final InheritanceCalculationService inheritanceCalculationService;

    @GetMapping("/calculate")
    public InheritanceMemberResponse calculate(@Valid @RequestBody InheritanceCalculationRequest request){
        return inheritanceCalculationService.calculateProblem(request);
    }

}
