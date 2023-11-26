package ru.ermolaev.services.report.generator.service.impl;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.report.generator.service.ReportParameterService;
import ru.ermolaev.services.report.models.constant.ReportTemplate;
import ru.ermolaev.services.report.models.model.report.SubscriberInfo;
import ru.ermolaev.services.report.models.model.report.container.SubscriberPaymentListReportData;

import java.util.HashMap;
import java.util.Map;

@Service
public class SubscriberPaymentListReportParameterService implements ReportParameterService {

    @Override
    public Map<String, Object> prepareReportParams(Object reportRawData) {
        SubscriberPaymentListReportData data = (SubscriberPaymentListReportData) reportRawData;

        JRBeanCollectionDataSource paymentDataset =
                new JRBeanCollectionDataSource(data.getPaymentInfoList());

        Map<String, Object> parameterMap = new HashMap<>();

        SubscriberInfo subscriberInfo = data.getSubscriberInfo();

        parameterMap.put("firstname", subscriberInfo.getFirstname());
        parameterMap.put("lastname", subscriberInfo.getLastname());
        parameterMap.put("patronymic", subscriberInfo.getPatronymic());
        parameterMap.put("accountNumber", subscriberInfo.getAccountNumber());
        parameterMap.put("city", subscriberInfo.getCity());
        parameterMap.put("street", subscriberInfo.getStreet());
        parameterMap.put("house", subscriberInfo.getHouse());
        parameterMap.put("flat", subscriberInfo.getFlat());
        parameterMap.put("connectionDate", subscriberInfo.getConnectionDate());
        parameterMap.put("balance", subscriberInfo.getBalance());
        parameterMap.put("paymentDataset", paymentDataset);

        return parameterMap;
    }

    @Override
    public boolean support(ReportTemplate template) {
        return ReportTemplate.SUBSCRIBER_PAYMENT_LIST.equals(template);
    }

}
