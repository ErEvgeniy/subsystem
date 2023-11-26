package ru.ermolaev.services.subscriber.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ermolaev.services.subscriber.manager.domain.ChargeTarget;

public interface ChargeTargetRepository extends JpaRepository<ChargeTarget, Long> {
}
