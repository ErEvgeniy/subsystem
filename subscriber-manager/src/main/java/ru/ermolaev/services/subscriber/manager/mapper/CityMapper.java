package ru.ermolaev.services.subscriber.manager.mapper;

import org.mapstruct.Mapper;
import ru.ermolaev.services.subscriber.manager.domain.City;
import ru.ermolaev.services.subscriber.manager.rest.dto.CityDto;

import java.util.List;

@Mapper(config = MappingConfig.class)
public interface CityMapper {

    CityDto toDto(City city);

    List<CityDto> toDtoList(List<City> cities);

    City toDomain(CityDto cityDto);

}
