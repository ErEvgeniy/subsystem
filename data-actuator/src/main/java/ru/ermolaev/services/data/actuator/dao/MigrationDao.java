package ru.ermolaev.services.data.actuator.dao;

public interface MigrationDao {

    boolean isMigrationLocked();

    void lockMigration();

    void unlockMigration();

}
