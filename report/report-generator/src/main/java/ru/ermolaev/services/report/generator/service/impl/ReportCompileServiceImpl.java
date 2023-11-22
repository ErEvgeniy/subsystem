package ru.ermolaev.services.report.generator.service.impl;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.report.generator.compiler.ReportCompiler;
import ru.ermolaev.services.report.generator.exception.ReportGenerationException;
import ru.ermolaev.services.report.generator.service.ReportCompileService;
import ru.ermolaev.services.report.models.constant.ReportTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "compiledJasperReportsCache")
public class ReportCompileServiceImpl implements ReportCompileService {

    private final List<ReportCompiler> reportCompilers;

    @Override
    @Cacheable(key = "#template.name()")
    public JasperReport getCompiledJasperReport(ReportTemplate template) {
        return reportCompilers.stream()
                .filter(reportCompiler -> reportCompiler.support(template))
                .findFirst()
                .orElseThrow(() -> new ReportGenerationException("Template", "Unexpected report template"))
                .compile();
    }

}
