package ru.ermolaev.services.subscriber.manager.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ChargeDto {

    private static final long serialVersionUID = -7832532223596571194L;

    private Long id;

    @NotNull
    private LocalDate chargeDate;

    @NotNull
    private ChargeTargetDto chargeTarget;

    @NotNull
    private Long subscriberId;

    @NotNull
    private Float amount;

    private String period;

    private String comment;

}
