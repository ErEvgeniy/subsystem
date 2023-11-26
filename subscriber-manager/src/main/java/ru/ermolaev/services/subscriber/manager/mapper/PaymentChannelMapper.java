package ru.ermolaev.services.subscriber.manager.mapper;

import org.mapstruct.Mapper;
import ru.ermolaev.services.subscriber.manager.domain.PaymentChannel;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentChannelDto;

import java.util.List;

@Mapper(config = MappingConfig.class)
public interface PaymentChannelMapper {

    PaymentChannelDto toDto(PaymentChannel paymentChannel);

    List<PaymentChannelDto> toDtoList(List<PaymentChannel> paymentChannels);

    PaymentChannel toDomain(PaymentChannelDto paymentChannelDto);

}
