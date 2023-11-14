package ru.ermolaev.services.data.actuator.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public abstract class AbstractMigrationModel {

    private Long id;

    private WriteStrategy writeStrategy;

}
