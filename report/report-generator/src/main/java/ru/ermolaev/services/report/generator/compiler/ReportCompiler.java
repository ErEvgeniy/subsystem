package ru.ermolaev.services.report.generator.compiler;

import net.sf.jasperreports.engine.JasperReport;
import ru.ermolaev.services.report.models.constant.ReportTemplate;

public interface ReportCompiler {

    JasperReport compile();

    boolean support(ReportTemplate template);

}
