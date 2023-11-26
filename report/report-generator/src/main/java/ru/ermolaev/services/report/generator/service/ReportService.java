package ru.ermolaev.services.report.generator.service;

import ru.ermolaev.services.report.models.model.request.ReportGenerationRequest;

public interface ReportService {

    byte[] generateReport(ReportGenerationRequest reportGenerationRequest);

}
