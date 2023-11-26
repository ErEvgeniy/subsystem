package ru.ermolaev.services.subscriber.manager.mapper;

import org.mapstruct.Mapper;
import ru.ermolaev.services.subscriber.manager.domain.ChargeTarget;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeTargetDto;

import java.util.List;

@Mapper(config = MappingConfig.class)
public interface ChargeTargetMapper {

    ChargeTargetDto toDto(ChargeTarget chargeTarget);

    List<ChargeTargetDto> toDtoList(List<ChargeTarget> chargeTargets);

    ChargeTarget toDomain(ChargeTargetDto chargeTargetDto);

}
