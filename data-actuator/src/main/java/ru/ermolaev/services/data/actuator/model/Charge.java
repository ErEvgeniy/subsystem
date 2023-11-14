package ru.ermolaev.services.data.actuator.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
public class Charge extends AbstractMigrationModel {

    private LocalDate chargeDate;

    private Long chargeTargetId;

    private Long subscriberId;

    private Float amount;

    private String period;

    private String comment;

}
