package ru.ermolaev.services.subscriber.manager.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;

import java.util.List;
import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {

    @EntityGraph(value = "subscriber-and-address-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Subscriber> findById(Long id);

    @EntityGraph(value = "subscriber-and-address-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Subscriber> findAll();

}
