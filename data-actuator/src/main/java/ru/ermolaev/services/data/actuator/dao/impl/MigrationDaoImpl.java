package ru.ermolaev.services.data.actuator.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.ermolaev.services.data.actuator.dao.MigrationDao;

@Slf4j
@Repository
public class MigrationDaoImpl implements MigrationDao {

    private static final int MIGRATION_LOCK_ENTITY_ID = 1;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public MigrationDaoImpl(@Qualifier("migrationJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean isMigrationLocked() {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("LOCK_ID", MIGRATION_LOCK_ENTITY_ID);
        try {
            Boolean locked = jdbcTemplate.queryForObject(
                    "SELECT LOCKED FROM MIGRATION_LOCK WHERE LOCK_ID = :LOCK_ID", parameters, Boolean.class);
            return BooleanUtils.isTrue(locked);
        } catch (Exception ex) {
            log.error("Exception occurred while get migration lock status", ex);
            return false;
        }
    }

    @Override
    public void lockMigration() {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("LOCK_ID", MIGRATION_LOCK_ENTITY_ID)
                .addValue("LOCKED", Boolean.TRUE);
        try {
            jdbcTemplate.update(
                    "UPDATE MIGRATION_LOCK SET LOCKED = :LOCKED WHERE LOCK_ID = :LOCK_ID", parameters);
        } catch (Exception ex) {
            log.error("Exception occurred while lock migration status", ex);
            throw ex;
        }
    }

    @Override
    public void unlockMigration() {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("LOCK_ID", MIGRATION_LOCK_ENTITY_ID)
                .addValue("LOCKED", Boolean.FALSE);
        try {
            jdbcTemplate.update(
                    "UPDATE MIGRATION_LOCK SET LOCKED = :LOCKED WHERE LOCK_ID = :LOCK_ID", parameters);
        } catch (Exception ex) {
            log.error("Exception occurred while unlock migration status", ex);
            throw ex;
        }
    }

}
