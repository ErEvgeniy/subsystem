package ru.ermolaev.services.subscriber.manager.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SubscriberDto {

    private static final long serialVersionUID = -5910922683642706000L;

    private Long id;

    @NotBlank(message = "Firstname can not be blank")
    private String firstname;

    private String patronymic;

    @NotBlank(message = "Lastname can not be blank")
    private String lastname;

    @Size(min = 5, max = 5, message = "Contract number must contain 5 characters")
    @Pattern(regexp = "^[0-9]+$", message = "Contract number must contain only digits")
    private String contractNumber;

    @Size(min = 5, max = 5, message = "Account number must contain 5 characters")
    @Pattern(regexp = "^[0-9]+$", message = "Account number must contain only digits")
    private String accountNumber;

    @NotNull
    private CityDto city;

    @NotNull
    private StreetDto street;

    @NotNull
    private Integer house;

    @NotNull
    private Integer flat;

    private String phoneNumber;

    @Email
    private String email;

    @NotNull
    private Float balance;

    @NotNull
    private Boolean isActive;

    @NotNull
    @Past(message = "Connection date can not be in future")
    private LocalDate connectionDate;

}
