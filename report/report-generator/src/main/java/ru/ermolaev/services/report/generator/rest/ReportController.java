package ru.ermolaev.services.report.generator.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ermolaev.services.report.generator.service.ReportService;
import ru.ermolaev.services.report.models.model.request.ReportGenerationRequest;

@RestController
@RequestMapping("/rg/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/report")
    public byte[] generateReport(@RequestBody ReportGenerationRequest reportGenerationRequest) {
        return reportService.generateReport(reportGenerationRequest);
    }

}
