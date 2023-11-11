package ru.ermolaev.services.subscriber.manager.service;

import ru.ermolaev.services.subscriber.manager.rest.dto.StreetDto;

import java.util.List;

public interface StreetService {

    StreetDto findOneById(long id);

    List<StreetDto> findAll();

    StreetDto create(StreetDto dto);

    StreetDto update(StreetDto dto);

    void deleteById(long id);

}
