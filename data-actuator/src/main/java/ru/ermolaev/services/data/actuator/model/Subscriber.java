package ru.ermolaev.services.data.actuator.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
public class Subscriber extends AbstractMigrationModel {

    private Long id;

    private String fullName;

    private String firstname;

    private String patronymic;

    private String lastname;

    private String contractNumber;

    private String accountNumber;

    private Long cityId;

    private Long streetId;

    private Integer house;

    private Integer flat;

    private String phoneNumber;

    private String email;

    private Float balance;

    private Boolean isActive;

    private LocalDate connectionDate;

}
