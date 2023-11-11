package ru.ermolaev.services.subscriber.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ermolaev.services.subscriber.manager.domain.Payment;
import ru.ermolaev.services.subscriber.manager.exception.BusinessException;
import ru.ermolaev.services.subscriber.manager.exception.ValidationException;
import ru.ermolaev.services.subscriber.manager.mapper.PaymentMapper;
import ru.ermolaev.services.subscriber.manager.repository.PaymentChannelRepository;
import ru.ermolaev.services.subscriber.manager.repository.PaymentRepository;
import ru.ermolaev.services.subscriber.manager.repository.SubscriberRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentDto;
import ru.ermolaev.services.subscriber.manager.service.PaymentService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final PaymentChannelRepository paymentChannelRepository;

    private final SubscriberRepository subscriberRepository;

    private final PaymentMapper paymentMapper;

    @Override
    @Transactional(readOnly = true)
    public PaymentDto findOneById(long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Payment with id - {} not found", id);
                    return new EntityNotFoundException(String.format("Payment with id - %d not found", id));
                });
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> findAllBySubscriberId(long subscriberId) {
        List<Payment> payments = paymentRepository.findAllBySubscriberId(subscriberId);
        return paymentMapper.toDtoList(payments);
    }

    @Override
    @Transactional
    public PaymentDto create(PaymentDto dto) {
        Payment newPayment = paymentMapper.toDomain(dto);

        updatePaymentChannel(newPayment, dto);
        updatePaymentSubscriber(newPayment, dto);

        paymentRepository.save(newPayment);
        log.info("Payment: {} was created", newPayment);
        return paymentMapper.toDto(newPayment);
    }

    @Override
    @Transactional
    public PaymentDto update(PaymentDto dto) {
        Payment existPayment = paymentRepository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Payment with id - %d not found", dto.getId())));

        updatePaymentChannel(existPayment, dto);
        updatePaymentSubscriber(existPayment, dto);
        updatePaymentDate(existPayment, dto);
        updatePaymentAmount(existPayment, dto);
        updatePaymentPeriod(existPayment, dto);
        updatePaymentComment(existPayment, dto);

        paymentRepository.save(existPayment);
        log.info("Payment: {} was updated", existPayment);
        return paymentMapper.toDto(existPayment);
    }

    private void updatePaymentChannel(Payment target, PaymentDto source) {
        Long paymentChannelId = source.getPaymentChannel().getId();
        if (paymentChannelId == null) {
            log.error("PaymentChannel id is not present to update payment");
            throw new ValidationException("PaymentChannel id is not present to update payment");
        }
        paymentChannelRepository.findById(paymentChannelId)
                .ifPresentOrElse(target::setPaymentChannel, () -> {
                    log.error("PaymentChannel with id - {} not found to update payment", paymentChannelId);
                    throw new BusinessException(
                            String.format("PaymentChannel with id - %d not found to update payment",
                                    paymentChannelId));
                });
    }

    private void updatePaymentSubscriber(Payment target, PaymentDto source) {
        subscriberRepository.findById(source.getSubscriberId())
                .ifPresentOrElse(target::setSubscriber, () -> {
                    log.error("Subscriber with id - {} not found to update payment", source.getSubscriberId());
                    throw new EntityNotFoundException(
                            String.format("Subscriber with id - %d not found to update payment",
                                    source.getSubscriberId()));
                });
    }

    private void updatePaymentDate(Payment target, PaymentDto source) {
        if (!Objects.equals(target.getPaymentDate(), source.getPaymentDate())) {
            log.debug("PaymentDate of payment with id - {} was changed from {} to {}",
                    target.getId(), target.getPaymentDate(), source.getPaymentDate());
            target.setPaymentDate(source.getPaymentDate());
        }
    }

    private void updatePaymentAmount(Payment target, PaymentDto source) {
        if (!Objects.equals(target.getAmount(), source.getAmount())) {
            log.debug("Amount of payment with id - {} was changed from {} to {}",
                    target.getId(), target.getAmount(), source.getAmount());
            target.setAmount(source.getAmount());
        }
    }

    private void updatePaymentPeriod(Payment target, PaymentDto source) {
        if (!Objects.equals(target.getPeriod(), source.getPeriod())) {
            log.debug("Period of payment with id - {} was changed from {} to {}",
                    target.getId(), target.getPeriod(), source.getPeriod());
            target.setPeriod(source.getPeriod());
        }
    }

    private void updatePaymentComment(Payment target, PaymentDto source) {
        if (!Objects.equals(target.getComment(), source.getComment())) {
            log.debug("Comment of payment with id - {} was changed from {} to {}",
                    target.getId(), target.getComment(), source.getComment());
            target.setComment(source.getComment());
        }
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        paymentRepository.deleteById(id);
        log.info("Payment with id: {} was deleted", id);
    }

}
