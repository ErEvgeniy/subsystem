package ru.ermolaev.services.subscriber.manager.service;

import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentChannelDto;

import java.util.List;

public interface PaymentChannelService {

    PaymentChannelDto findOneById(long id);

    List<PaymentChannelDto> findAll();

    PaymentChannelDto create(PaymentChannelDto dto);

    PaymentChannelDto update(PaymentChannelDto dto);

    void deleteById(long id);

}
