package ru.ermolaev.services.subscriber.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ermolaev.services.subscriber.manager.domain.City;

public interface CityRepository extends JpaRepository<City, Long> {
}
