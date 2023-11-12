package ru.ermolaev.services.data.actuator.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMigrationModel {

    private WriteStrategy writeStrategy = WriteStrategy.CREATE;

}
