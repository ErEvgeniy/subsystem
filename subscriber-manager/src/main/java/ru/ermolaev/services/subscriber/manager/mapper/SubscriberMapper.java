package ru.ermolaev.services.subscriber.manager.mapper;

import org.mapstruct.Mapper;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.rest.dto.SubscriberDto;

import java.util.List;

@Mapper(config = MappingConfig.class, uses = {
        CityMapper.class,
        StreetMapper.class
})
public interface SubscriberMapper {

    SubscriberDto toDto(Subscriber subscriber);

    List<SubscriberDto> toDtoList(List<Subscriber> subscriberList);

    Subscriber toDomain(SubscriberDto subscriberDto);

}
