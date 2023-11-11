package ru.ermolaev.services.subscriber.manager.service;

import ru.ermolaev.services.subscriber.manager.rest.dto.SubscriberDto;

import java.util.List;

public interface SubscriberService {

    SubscriberDto findOneById(long id);

    List<SubscriberDto> findAll();

    SubscriberDto create(SubscriberDto dto);

    SubscriberDto update(SubscriberDto dto);

    void deleteById(long id);

}
