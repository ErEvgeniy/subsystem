package ru.ermolaev.services.report.generator.service;

import ru.ermolaev.services.report.models.constant.ReportTemplate;

import java.util.Map;

public interface ReportParameterService {

    Map<String, Object> prepareReportParams(Object reportRawData);

    boolean support(ReportTemplate reportTemplate);

}
