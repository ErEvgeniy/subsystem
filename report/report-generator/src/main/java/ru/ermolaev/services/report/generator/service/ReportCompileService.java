package ru.ermolaev.services.report.generator.service;

import net.sf.jasperreports.engine.JasperReport;
import ru.ermolaev.services.report.models.constant.ReportTemplate;

public interface ReportCompileService {

    JasperReport getCompiledJasperReport(ReportTemplate template);

}
