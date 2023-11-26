package ru.ermolaev.services.data.actuator.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
public class Subscriber extends AbstractMigrationModel {

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
