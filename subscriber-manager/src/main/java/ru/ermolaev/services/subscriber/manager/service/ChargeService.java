package ru.ermolaev.services.subscriber.manager.service;

import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeDto;

import java.util.List;

public interface ChargeService {

    ChargeDto findOneById(long id);

    List<ChargeDto> findAllBySubscriberId(long subscriberId);

    ChargeDto create(ChargeDto dto);

    ChargeDto update(ChargeDto dto);

    void deleteById(long id);

}
