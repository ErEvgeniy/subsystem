package ru.ermolaev.services.report.models.model.report;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChargeInfo implements Serializable {

    private static final long serialVersionUID = -85745419001505687L;

    private String chargeDate;

    private String chargeAmount;

}
