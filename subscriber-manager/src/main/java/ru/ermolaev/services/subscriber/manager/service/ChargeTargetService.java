package ru.ermolaev.services.subscriber.manager.service;

import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeTargetDto;

import java.util.List;

public interface ChargeTargetService {

    ChargeTargetDto findOneById(long id);

    List<ChargeTargetDto> findAll();

    ChargeTargetDto create(ChargeTargetDto dto);

    ChargeTargetDto update(ChargeTargetDto dto);

    void deleteById(long id);

}
