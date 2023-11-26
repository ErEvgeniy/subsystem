package ru.ermolaev.services.subscriber.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ermolaev.services.subscriber.manager.domain.ChargeTarget;
import ru.ermolaev.services.subscriber.manager.mapper.ChargeTargetMapper;
import ru.ermolaev.services.subscriber.manager.repository.ChargeTargetRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeTargetDto;
import ru.ermolaev.services.subscriber.manager.service.ChargeTargetService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChargeTargetServiceImpl implements ChargeTargetService {

    private final ChargeTargetRepository chargeTargetRepository;

    private final ChargeTargetMapper chargeTargetMapper;

    @Override
    @Transactional(readOnly = true)
    public ChargeTargetDto findOneById(long id) {
        ChargeTarget chargeTarget = chargeTargetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("ChargeTarget with id - {} not found", id);
                    return new EntityNotFoundException(String.format("ChargeTarget with id - %d not found", id));
                });
        return chargeTargetMapper.toDto(chargeTarget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChargeTargetDto> findAll() {
        List<ChargeTarget> chargeTargets = chargeTargetRepository.findAll();
        return chargeTargetMapper.toDtoList(chargeTargets);
    }

    @Override
    @Transactional
    public ChargeTargetDto create(ChargeTargetDto dto) {
        ChargeTarget newChargeTarget = chargeTargetMapper.toDomain(dto);
        chargeTargetRepository.save(newChargeTarget);
        log.info("ChargeTarget: {} was created", newChargeTarget);
        return chargeTargetMapper.toDto(newChargeTarget);
    }

    @Override
    @Transactional
    public ChargeTargetDto update(ChargeTargetDto dto) {
        ChargeTarget existChargeTarget = chargeTargetRepository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("ChargeTarget with id - %d not found", dto.getId())));

        if (!Objects.equals(existChargeTarget.getName(), dto.getName())) {
            log.debug("Name of charge target with id - {} was changed from {} to {}",
                    existChargeTarget.getId(), existChargeTarget.getName(), dto.getName());
            existChargeTarget.setName(dto.getName());
        }

        chargeTargetRepository.save(existChargeTarget);
        log.info("ChargeTarget: {} was updated", existChargeTarget);
        return chargeTargetMapper.toDto(existChargeTarget);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        chargeTargetRepository.deleteById(id);
        log.info("ChargeTarget with id: {} was deleted", id);
    }

}
