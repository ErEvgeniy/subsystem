package ru.ermolaev.services.report.generator.compiler.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ermolaev.services.report.models.constant.ReportTemplate;

@Slf4j
@Component
public class SubscriberPaymentListReportCompiler extends AbstractReportCompiler {

    @Override
    public boolean support(ReportTemplate template) {
        return ReportTemplate.SUBSCRIBER_PAYMENT_LIST.equals(template);
    }

    @Override
    protected String getReportTemplateFileName() {
        return "subscriber-payments.jrxml";
    }

}
