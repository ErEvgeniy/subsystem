package ru.ermolaev.services.subscriber.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ermolaev.services.subscriber.manager.domain.PaymentChannel;
import ru.ermolaev.services.subscriber.manager.mapper.PaymentChannelMapper;
import ru.ermolaev.services.subscriber.manager.repository.PaymentChannelRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentChannelDto;
import ru.ermolaev.services.subscriber.manager.service.PaymentChannelService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentChannelServiceImpl implements PaymentChannelService {

    private final PaymentChannelRepository paymentChannelRepository;

    private final PaymentChannelMapper paymentChannelMapper;

    @Override
    @Transactional(readOnly = true)
    public PaymentChannelDto findOneById(long id) {
        PaymentChannel paymentChannel = paymentChannelRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("PaymentChannel with id - {} not found", id);
                    return new EntityNotFoundException(String.format("PaymentChannel with id - %d not found", id));
                });
        return paymentChannelMapper.toDto(paymentChannel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentChannelDto> findAll() {
        List<PaymentChannel> paymentChannels = paymentChannelRepository.findAll();
        return paymentChannelMapper.toDtoList(paymentChannels);
    }

    @Override
    @Transactional
    public PaymentChannelDto create(PaymentChannelDto dto) {
        PaymentChannel newPaymentChannel = paymentChannelMapper.toDomain(dto);
        paymentChannelRepository.save(newPaymentChannel);
        log.info("PaymentChannel: {} was created", newPaymentChannel);
        return paymentChannelMapper.toDto(newPaymentChannel);
    }

    @Override
    @Transactional
    public PaymentChannelDto update(PaymentChannelDto dto) {
        PaymentChannel existPaymentChannel = paymentChannelRepository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("PaymentChannel with id - %d not found", dto.getId())));

        if (!Objects.equals(existPaymentChannel.getName(), dto.getName())) {
            log.debug("Name of payment channel with id - {} was changed from {} to {}",
                    existPaymentChannel.getId(), existPaymentChannel.getName(), dto.getName());
            existPaymentChannel.setName(dto.getName());
        }

        paymentChannelRepository.save(existPaymentChannel);
        log.info("PaymentChannel: {} was updated", existPaymentChannel);
        return paymentChannelMapper.toDto(existPaymentChannel);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        paymentChannelRepository.deleteById(id);
        log.info("PaymentChannel with id: {} was deleted", id);
    }

}
