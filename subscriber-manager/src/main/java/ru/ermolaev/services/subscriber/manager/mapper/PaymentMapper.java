package ru.ermolaev.services.subscriber.manager.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ermolaev.services.subscriber.manager.domain.Payment;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentDto;

import java.util.List;

@Mapper(config = MappingConfig.class, uses = {
        PaymentChannelMapper.class
})
public interface PaymentMapper {

    @Mapping(source = "payment.subscriber.id", target = "subscriberId")
    PaymentDto toDto(Payment payment);

    List<PaymentDto> toDtoList(List<Payment> payments);

    Payment toDomain(PaymentDto paymentDto);

}
