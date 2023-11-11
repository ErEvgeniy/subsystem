package ru.ermolaev.services.subscriber.manager.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class StreetDto {

    private static final long serialVersionUID = 5814174909548016000L;

    private Long id;

    @NotBlank(message = "Street name can not be blank")
    private String name;

}
