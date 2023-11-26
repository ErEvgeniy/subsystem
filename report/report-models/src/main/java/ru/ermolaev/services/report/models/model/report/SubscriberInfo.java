package ru.ermolaev.services.report.models.model.report;

import lombok.Data;

import java.io.Serializable;

@Data
public class SubscriberInfo implements Serializable {

    private static final long serialVersionUID = -657965675478001L;

    private String firstname;

    private String patronymic;

    private String lastname;

    private String accountNumber;

    private String city;

    private String street;

    private int house;

    private int flat;

    private String connectionDate;

    private float balance;

}
