package ru.ermolaev.services.report.generator.compiler.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.ermolaev.services.report.generator.exception.ReportGenerationException;
import ru.ermolaev.services.report.generator.compiler.ReportCompiler;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractReportCompiler implements ReportCompiler {

    private static final String REPORT_FOLDER_PATH = "reports/";

    @Override
    public JasperReport compile() {
        String reportTemplateFilePath = getReportTemplateFilePath();
        Resource resource = new ClassPathResource(reportTemplateFilePath);

        try (InputStream inputStream = resource.getInputStream()) {

            try {
                return JasperCompileManager.compileReport(inputStream);
            } catch (JRException ex) {
                log.error("Compile {} failed", getReportTemplateFileName(), ex);
                throw new ReportGenerationException("Compile", "Compile report failed");
            }

        } catch (IOException ex) {
            log.error("Error while process report resource", ex);
            throw new ReportGenerationException("Resource", "Report resource processing error");
        }
    }

    protected abstract String getReportTemplateFileName();

    private String getReportTemplateFilePath() {
        return REPORT_FOLDER_PATH + getReportTemplateFileName();
    }

}
