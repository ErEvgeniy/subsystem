package ru.ermolaev.services.subscriber.manager.service;

import ru.ermolaev.services.report.models.constant.ReportTemplate;

public interface ReportDataService {

    byte[] prepareReportData(long subscriberId, ReportTemplate reportTemplate);

}
