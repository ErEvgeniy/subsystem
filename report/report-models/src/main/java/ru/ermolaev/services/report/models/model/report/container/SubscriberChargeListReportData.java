package ru.ermolaev.services.report.models.model.report.container;

import lombok.Data;
import ru.ermolaev.services.report.models.model.report.ChargeInfo;
import ru.ermolaev.services.report.models.model.report.SubscriberInfo;

import java.io.Serializable;
import java.util.List;

@Data
public class SubscriberChargeListReportData implements Serializable {

    private static final long serialVersionUID = -1575595665012432051L;

    private SubscriberInfo subscriberInfo;

    private List<ChargeInfo> chargeInfoList;

}
