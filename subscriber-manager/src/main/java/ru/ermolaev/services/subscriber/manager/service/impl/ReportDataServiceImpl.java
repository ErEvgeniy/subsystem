package ru.ermolaev.services.subscriber.manager.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.report.models.constant.ReportTemplate;
import ru.ermolaev.services.subscriber.manager.exception.BusinessException;
import ru.ermolaev.services.subscriber.manager.service.ReportDataCollectService;
import ru.ermolaev.services.subscriber.manager.service.ReportDataService;
import ru.ermolaev.services.subscriber.manager.service.SerializationService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportDataServiceImpl implements ReportDataService {

    private final SerializationService serializationService;

    private final List<ReportDataCollectService> reportDataCollectServices;

    @Override
    public byte[] prepareReportData(long subscriberId, ReportTemplate reportTemplate) {
        Object reportData = reportDataCollectServices.stream()
                .filter(reportDataCollectService -> reportDataCollectService.support(reportTemplate))
                .findFirst()
                .orElseThrow(() -> new BusinessException("Unsupported template"))
                .prepare(subscriberId);

        return serializationService.serialize(reportData);
    }

}
