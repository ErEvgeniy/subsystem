package ru.ermolaev.services.subscriber.manager.service;

import ru.ermolaev.services.subscriber.manager.rest.dto.CityDto;

import java.util.List;

public interface CityService {

    CityDto findOneById(long id);

    List<CityDto> findAll();

    CityDto create(CityDto dto);

    CityDto update(CityDto dto);

    void deleteById(long id);

}
