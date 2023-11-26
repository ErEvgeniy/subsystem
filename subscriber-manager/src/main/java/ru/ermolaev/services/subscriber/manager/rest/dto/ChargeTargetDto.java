package ru.ermolaev.services.subscriber.manager.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChargeTargetDto {

    private static final long serialVersionUID = -2561792805163482966L;

    private Long id;

    @NotBlank(message = "Charge target name can not be blank")
    private String name;

}
