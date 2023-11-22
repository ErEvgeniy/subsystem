package ru.ermolaev.services.report.generator.compiler.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ermolaev.services.report.models.constant.ReportTemplate;

@Slf4j
@Component
public class SubscriberChargeListReportCompiler extends AbstractReportCompiler {

    @Override
    public boolean support(ReportTemplate template) {
        return ReportTemplate.SUBSCRIBER_CHARGE_LIST.equals(template);
    }

    @Override
    protected String getReportTemplateFileName() {
        return "subscriber-charges.jrxml";
    }

}
