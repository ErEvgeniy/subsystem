package ru.ermolaev.services.subscriber.manager.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.report.models.constant.ReportFileFormat;
import ru.ermolaev.services.report.models.model.request.ReportGenerationRequest;
import ru.ermolaev.services.report.models.constant.ReportTemplate;
import ru.ermolaev.services.subscriber.manager.exception.BusinessException;
import ru.ermolaev.services.subscriber.manager.integration.feign.ReportGeneratorServiceProxy;
import ru.ermolaev.services.subscriber.manager.service.ReportDataService;
import ru.ermolaev.services.subscriber.manager.service.ReportService;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportDataService reportDataService;

    private final ReportGeneratorServiceProxy reportGeneratorServiceProxy;

    @Override
    public byte[] generateReport(long subscriberId, String templateName) {
        Set<String> supportedTemplates = Set.of(
                ReportTemplate.SUBSCRIBER_PAYMENT_LIST.name(),
                ReportTemplate.SUBSCRIBER_CHARGE_LIST.name());

        if (!supportedTemplates.contains(templateName)) {
            log.error("Unsupported template name: {}", templateName);
            throw new BusinessException("Unsupported template");
        }

        ReportTemplate reportTemplate = ReportTemplate.valueOf(templateName);
        byte[] reportData = reportDataService.prepareReportData(subscriberId, reportTemplate);

        ReportGenerationRequest reportGenerationRequest = new ReportGenerationRequest();
        reportGenerationRequest.setData(reportData);
        reportGenerationRequest.setTemplate(reportTemplate);
        reportGenerationRequest.setFileFormat(ReportFileFormat.PDF);

        return reportGeneratorServiceProxy.generateReport(reportGenerationRequest);
    }

}
