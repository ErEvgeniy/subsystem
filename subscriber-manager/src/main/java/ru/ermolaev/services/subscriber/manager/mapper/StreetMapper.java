package ru.ermolaev.services.subscriber.manager.mapper;

import org.mapstruct.Mapper;
import ru.ermolaev.services.subscriber.manager.domain.Street;
import ru.ermolaev.services.subscriber.manager.rest.dto.StreetDto;

import java.util.List;

@Mapper(config = MappingConfig.class)
public interface StreetMapper {

    StreetDto toDto(Street street);

    List<StreetDto> toDtoList(List<Street> streets);

    Street toDomain(StreetDto streetDto);

}
