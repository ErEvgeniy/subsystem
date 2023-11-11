package ru.ermolaev.services.subscriber.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ermolaev.services.subscriber.manager.domain.PaymentChannel;

public interface PaymentChannelRepository extends JpaRepository<PaymentChannel, Long> {
}
