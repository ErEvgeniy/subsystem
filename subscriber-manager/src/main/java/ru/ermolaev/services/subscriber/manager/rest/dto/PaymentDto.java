package ru.ermolaev.services.subscriber.manager.rest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentDto {

    private static final long serialVersionUID = 1990678596517671544L;

    private Long id;

    @NotNull
    private LocalDate paymentDate;

    @NotNull
    private PaymentChannelDto paymentChannel;

    @NotNull
    private Long subscriberId;

    @NotNull
    private Float amount;

    private String period;

    private String comment;

}
