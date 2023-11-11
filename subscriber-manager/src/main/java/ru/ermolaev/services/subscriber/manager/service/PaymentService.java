package ru.ermolaev.services.subscriber.manager.service;

import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentDto;

import java.util.List;

public interface PaymentService {

    PaymentDto findOneById(long id);

    List<PaymentDto> findAllBySubscriberId(long subscriberId);

    PaymentDto create(PaymentDto dto);

    PaymentDto update(PaymentDto dto);

    void deleteById(long id);

}
