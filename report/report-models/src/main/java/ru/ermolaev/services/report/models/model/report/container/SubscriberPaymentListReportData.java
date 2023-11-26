package ru.ermolaev.services.report.models.model.report.container;

import lombok.Data;
import ru.ermolaev.services.report.models.model.report.PaymentInfo;
import ru.ermolaev.services.report.models.model.report.SubscriberInfo;

import java.io.Serializable;
import java.util.List;

@Data
public class SubscriberPaymentListReportData implements Serializable {

    private static final long serialVersionUID = 97525956654732004L;

    private SubscriberInfo subscriberInfo;

    private List<PaymentInfo> paymentInfoList;

}
