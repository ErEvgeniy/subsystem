package ru.ermolaev.services.subscriber.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ermolaev.services.subscriber.manager.domain.Charge;
import ru.ermolaev.services.subscriber.manager.exception.BusinessException;
import ru.ermolaev.services.subscriber.manager.exception.ValidationException;
import ru.ermolaev.services.subscriber.manager.mapper.ChargeMapper;
import ru.ermolaev.services.subscriber.manager.repository.ChargeRepository;
import ru.ermolaev.services.subscriber.manager.repository.ChargeTargetRepository;
import ru.ermolaev.services.subscriber.manager.repository.SubscriberRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeDto;
import ru.ermolaev.services.subscriber.manager.service.ChargeService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargeServiceImpl implements ChargeService {

    private final ChargeRepository chargeRepository;

    private final ChargeTargetRepository chargeTargetRepository;

    private final SubscriberRepository subscriberRepository;

    private final ChargeMapper chargeMapper;

    @Override
    @Transactional(readOnly = true)
    public ChargeDto findOneById(long id) {
        Charge charge = chargeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Charge with id - {} not found", id);
                    return new EntityNotFoundException(String.format("Charge with id - %d not found", id));
                });
        return chargeMapper.toDto(charge);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargeDto> findAllBySubscriberId(long subscriberId) {
        List<Charge> payments = chargeRepository.findAllBySubscriberId(subscriberId);
        return chargeMapper.toDtoList(payments);
    }

    @Override
    @Transactional
    public ChargeDto create(ChargeDto dto) {
        Charge newCharge = chargeMapper.toDomain(dto);

        updateChargeTarget(newCharge, dto);
        updateChargeSubscriber(newCharge, dto);

        chargeRepository.save(newCharge);
        log.info("Charge: {} was created", newCharge);
        return chargeMapper.toDto(newCharge);
    }

    @Override
    @Transactional
    public ChargeDto update(ChargeDto dto) {
        Charge existCharge = chargeRepository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Charge with id - %d not found", dto.getId())));

        updateChargeTarget(existCharge, dto);
        updateChargeSubscriber(existCharge, dto);
        updateChargeDate(existCharge, dto);
        updateChargeAmount(existCharge, dto);
        updateChargePeriod(existCharge, dto);
        updateChargeComment(existCharge, dto);

        chargeRepository.save(existCharge);
        log.info("Charge: {} was updated", existCharge);
        return chargeMapper.toDto(existCharge);
    }

    private void updateChargeTarget(Charge target, ChargeDto source) {
        Long chargeTargetId = source.getChargeTarget().getId();
        if (chargeTargetId == null) {
            log.error("ChargeTarget id is not present to update charge");
            throw new ValidationException("ChargeTarget id is not present to update charge");
        }
        chargeTargetRepository.findById(chargeTargetId)
                .ifPresentOrElse(target::setChargeTarget, () -> {
                    log.error("ChargeTarget with id - {} not found to update charge", chargeTargetId);
                    throw new BusinessException(
                            String.format("ChargeTarget with id - %d not found to update charge",
                                    chargeTargetId));
                });
    }

    private void updateChargeSubscriber(Charge target, ChargeDto source) {
        subscriberRepository.findById(source.getSubscriberId())
                .ifPresentOrElse(target::setSubscriber, () -> {
                    log.error("Subscriber with id - {} not found to update charge", source.getSubscriberId());
                    throw new EntityNotFoundException(
                            String.format("Subscriber with id - %d not found to update charge",
                                    source.getSubscriberId()));
                });
    }

    private void updateChargeDate(Charge target, ChargeDto source) {
        if (!Objects.equals(target.getChargeDate(), source.getChargeDate())) {
            log.debug("ChargeDate of charge with id - {} was changed from {} to {}",
                    target.getId(), target.getChargeDate(), source.getChargeDate());
            target.setChargeDate(source.getChargeDate());
        }
    }

    private void updateChargeAmount(Charge target, ChargeDto source) {
        if (!Objects.equals(target.getAmount(), source.getAmount())) {
            log.debug("Amount of charge with id - {} was changed from {} to {}",
                    target.getId(), target.getAmount(), source.getAmount());
            target.setAmount(source.getAmount());
        }
    }

    private void updateChargePeriod(Charge target, ChargeDto source) {
        if (!Objects.equals(target.getPeriod(), source.getPeriod())) {
            log.debug("Period of charge with id - {} was changed from {} to {}",
                    target.getId(), target.getPeriod(), source.getPeriod());
            target.setPeriod(source.getPeriod());
        }
    }

    private void updateChargeComment(Charge target, ChargeDto source) {
        if (!Objects.equals(target.getComment(), source.getComment())) {
            log.debug("Comment of charge with id - {} was changed from {} to {}",
                    target.getId(), target.getComment(), source.getComment());
            target.setComment(source.getComment());
        }
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        chargeRepository.deleteById(id);
        log.info("Charge with id: {} was deleted", id);
    }

}
