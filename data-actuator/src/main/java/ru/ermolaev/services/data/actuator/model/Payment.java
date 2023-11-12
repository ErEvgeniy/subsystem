package ru.ermolaev.services.data.actuator.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class Payment extends AbstractMigrationModel {

    private Long id;

    private LocalDate paymentDate;

    private Long paymentChannelId;

    private Long subscriberId;

    private Float amount;

    private String period;

    private String comment;

}
