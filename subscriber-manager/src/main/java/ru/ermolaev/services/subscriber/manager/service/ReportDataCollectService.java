package ru.ermolaev.services.subscriber.manager.service;

import ru.ermolaev.services.report.models.constant.ReportTemplate;

public interface ReportDataCollectService {

    Object prepare(long subscriberId);

    boolean support(ReportTemplate template);

}
