package ru.ermolaev.services.subscriber.manager.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentChannelDto {

    private static final long serialVersionUID = -4807118048953836928L;

    private Long id;

    @NotBlank(message = "Payment channel name can not be blank")
    private String name;

}
