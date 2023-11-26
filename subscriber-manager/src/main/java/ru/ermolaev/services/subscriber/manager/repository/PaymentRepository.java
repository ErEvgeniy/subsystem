package ru.ermolaev.services.subscriber.manager.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.ermolaev.services.subscriber.manager.domain.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @EntityGraph(value = "payment-full-join-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Payment> findById(Long id);

    @EntityGraph(value = "payment-full-join-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Payment> findAllBySubscriberId(Long subscriberId);

}
