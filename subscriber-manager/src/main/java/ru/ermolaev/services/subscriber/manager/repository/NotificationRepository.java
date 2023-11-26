package ru.ermolaev.services.subscriber.manager.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ermolaev.services.subscriber.manager.domain.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @EntityGraph(value = "notification-and-subscriber-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Notification> findById(Long id);

    @EntityGraph(value = "notification-and-subscriber-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Notification> findAllBySubscriberId(Long subscriberId);

}
