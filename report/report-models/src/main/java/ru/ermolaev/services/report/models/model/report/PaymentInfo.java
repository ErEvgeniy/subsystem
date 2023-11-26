package ru.ermolaev.services.report.models.model.report;

import lombok.Data;

import java.io.Serializable;

@Data
public class PaymentInfo implements Serializable {

    private static final long serialVersionUID = 1058547815477519L;

    private String paymentDate;

    private String paymentAmount;

}
