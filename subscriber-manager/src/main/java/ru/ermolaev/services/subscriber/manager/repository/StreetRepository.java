package ru.ermolaev.services.subscriber.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ermolaev.services.subscriber.manager.domain.Street;

public interface StreetRepository extends JpaRepository<Street, Long> {
}
