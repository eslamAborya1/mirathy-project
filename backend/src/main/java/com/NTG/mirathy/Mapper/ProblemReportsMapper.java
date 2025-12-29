package com.NTG.mirathy.Mapper;

import com.NTG.mirathy.DTOs.response.ProblemReportResponse;
import com.NTG.mirathy.Entity.ProblemReport;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProblemReportsMapper {
    ProblemReportResponse toProblemReportResponse(ProblemReport problemReport);
    List<ProblemReportResponse> toProblemReportResponses(List<ProblemReport> problemReports);
}
