package ru.ermolaev.services.subscriber.manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ermolaev.services.subscriber.manager.domain.Notification;
import ru.ermolaev.services.subscriber.manager.rest.dto.NotificationDto;

import java.util.List;

@Mapper(config = MappingConfig.class)
public interface NotificationMapper {

    @Mapping(source = "domain.subscriber.id", target = "subscriberId")
    NotificationDto toDto(Notification domain);

    List<NotificationDto> toDtoList(List<Notification> domainList);

    Notification toDomain(NotificationDto dto);

}
