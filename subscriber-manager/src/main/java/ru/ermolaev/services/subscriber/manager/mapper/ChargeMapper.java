package ru.ermolaev.services.subscriber.manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ermolaev.services.subscriber.manager.domain.Charge;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeDto;

import java.util.List;

@Mapper(config = MappingConfig.class, uses = {
        ChargeTargetMapper.class
})
public interface ChargeMapper {

    @Mapping(source = "charge.subscriber.id", target = "subscriberId")
    ChargeDto toDto(Charge charge);

    List<ChargeDto> toDtoList(List<Charge> charges);

    Charge toDomain(ChargeDto chargeDto);

}
