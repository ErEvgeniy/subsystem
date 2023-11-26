package ru.ermolaev.services.subscriber.manager.service;

public interface ReportService {

    byte[] generateReport(long subscriberId, String templateName);

}
