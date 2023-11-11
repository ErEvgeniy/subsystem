package ru.ermolaev.services.subscriber.manager.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CityDto {

    private static final long serialVersionUID = -4152869933590270017L;

    private Long id;

    @NotBlank(message = "City name can not be blank")
    private String name;

}
