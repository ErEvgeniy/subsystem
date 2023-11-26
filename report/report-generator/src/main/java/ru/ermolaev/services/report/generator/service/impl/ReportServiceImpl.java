package ru.ermolaev.services.report.generator.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.report.generator.exception.ReportGenerationException;
import ru.ermolaev.services.report.generator.service.DeserializationService;
import ru.ermolaev.services.report.generator.service.ReportCompileService;
import ru.ermolaev.services.report.generator.service.ReportParameterService;
import ru.ermolaev.services.report.generator.service.ReportService;
import ru.ermolaev.services.report.models.constant.ReportFileFormat;
import ru.ermolaev.services.report.models.model.request.ReportGenerationRequest;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final DeserializationService deserializationService;

    private final ReportCompileService reportCompileService;

    private final List<ReportParameterService> reportParameterServices;

    @Override
    public byte[] generateReport(ReportGenerationRequest reportGenerationRequest) {
        Object reportData = deserializationService.deserialize(reportGenerationRequest.getData());
        Map<String, Object> reportParams = reportParameterServices.stream()
                .filter(reportParamService -> reportParamService.support(reportGenerationRequest.getTemplate()))
                .findFirst()
                .orElseThrow(() -> new ReportGenerationException("Template", "Unexpected report template"))
                .prepareReportParams(reportData);

        JasperReport report = reportCompileService.getCompiledJasperReport(reportGenerationRequest.getTemplate());
        JasperPrint print;
        try {
            print = JasperFillManager.fillReport(report, reportParams, new JREmptyDataSource());
        } catch (JRException ex) {
            log.error("Error occurred while fill report", ex);
            throw new ReportGenerationException("Generate", "Failed fill report");
        }

        return export(print, reportGenerationRequest.getFileFormat());
    }

    private byte[] export(JasperPrint jasperPrint, ReportFileFormat fileFormat) {
        try {
            switch (fileFormat) {
                case PDF -> {
                    return JasperExportManager.exportReportToPdf(jasperPrint);
                }
                default -> throw new ReportGenerationException("Template", "Unsupported report format");
            }
        } catch (JRException ex) {
            log.error("Error occurred while export to: {}", fileFormat.name(), ex);
            throw new ReportGenerationException("Export", "Export failed");
        }
    }

}
