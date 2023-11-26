package ru.ermolaev.services.subscriber.manager.util;

import jakarta.persistence.Query;
import org.hibernate.SessionFactory;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

public class TestRepositoryUtils {

    public static void clearSessionStatistic(TestEntityManager testEntityManager) {
        SessionFactory sessionFactory = testEntityManager.getEntityManager()
                .getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        sessionFactory.getStatistics().setStatisticsEnabled(true);
        sessionFactory.getStatistics().clear();
    }

    public static void assertSessionStatementExecutionCount(
            TestEntityManager testEntityManager,
            long expectedStatementExecutions
    ) {
        SessionFactory sessionFactory = testEntityManager.getEntityManager()
                .getEntityManagerFactory()
                .unwrap(SessionFactory.class);
        assertThat(sessionFactory.getStatistics().getPrepareStatementCount()).isEqualTo(expectedStatementExecutions);
    }

    public static int countExistedEntities(TestEntityManager testEntityManager, String entityName) {
        String sqlQuery = "SELECT COUNT(*) FROM " + entityName;
        Query countQuery = testEntityManager.getEntityManager().createQuery(sqlQuery);
        Long count = (Long) countQuery.getSingleResult();
        return count.intValue();
    }

    public static int countExistedEntitiesBySubscriber(
            TestEntityManager testEntityManager, String entityName, long subscriberId) {
        String sqlQuery = "SELECT COUNT(*) FROM " + entityName + " WHERE subscriber.id = " + subscriberId;
        Query countQuery = testEntityManager.getEntityManager().createQuery(sqlQuery);
        Long count = (Long) countQuery.getSingleResult();
        return count.intValue();
    }

}
