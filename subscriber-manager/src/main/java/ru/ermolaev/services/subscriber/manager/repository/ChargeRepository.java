package ru.ermolaev.services.subscriber.manager.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ermolaev.services.subscriber.manager.domain.Charge;

import java.util.List;
import java.util.Optional;

public interface ChargeRepository extends JpaRepository<Charge, Long> {

    @EntityGraph(value = "charge-full-join-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Charge> findById(Long id);

    @EntityGraph(value = "charge-full-join-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Charge> findAllBySubscriberId(Long subscriberId);

}
