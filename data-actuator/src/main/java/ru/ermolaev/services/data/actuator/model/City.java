package ru.ermolaev.services.data.actuator.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class City extends AbstractMigrationModel {

    private Long id;

    private String name;

}
