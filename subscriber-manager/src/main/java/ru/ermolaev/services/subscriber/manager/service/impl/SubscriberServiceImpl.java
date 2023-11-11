package ru.ermolaev.services.subscriber.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.exception.BusinessException;
import ru.ermolaev.services.subscriber.manager.exception.ValidationException;
import ru.ermolaev.services.subscriber.manager.mapper.SubscriberMapper;
import ru.ermolaev.services.subscriber.manager.repository.CityRepository;
import ru.ermolaev.services.subscriber.manager.repository.StreetRepository;
import ru.ermolaev.services.subscriber.manager.repository.SubscriberRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.SubscriberDto;
import ru.ermolaev.services.subscriber.manager.service.SubscriberService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriberServiceImpl implements SubscriberService {

    private final SubscriberRepository subscriberRepository;

    private final CityRepository cityRepository;

    private final StreetRepository streetRepository;

    private final SubscriberMapper subscriberMapper;

    @Override
    @Transactional(readOnly = true)
    public SubscriberDto findOneById(long id) {
        Subscriber subscriber = subscriberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Subscriber with id - {} not found", id);
                    return new EntityNotFoundException(String.format("Subscriber with id - %d not found", id));
                });
        return subscriberMapper.toDto(subscriber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriberDto> findAll() {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        return subscriberMapper.toDtoList(subscribers);
    }

    @Override
    @Transactional
    public SubscriberDto create(SubscriberDto dto) {
        Subscriber newSub = subscriberMapper.toDomain(dto);

        newSub.setBalance(0F);
        updateSubscriberCity(newSub, dto);
        updateSubscriberStreet(newSub, dto);

        subscriberRepository.save(newSub);
        return subscriberMapper.toDto(newSub);
    }

    @Override
    @Transactional
    public SubscriberDto update(SubscriberDto dto) {
        Subscriber existSub = subscriberRepository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Subscriber with id - %d not found", dto.getId())));

        if (!Objects.equals(existSub.getBalance(), dto.getBalance())) {
            log.error("Subscriber balance cannot be updated");
            throw new ValidationException("Subscriber balance cannot be updated");
        }

        updateSubscriberFields(existSub, dto);

        subscriberRepository.save(existSub);
        return subscriberMapper.toDto(existSub);
    }

    private void updateSubscriberFields(Subscriber target, SubscriberDto source) {
        updateSubscriberCity(target, source);
        updateSubscriberStreet(target, source);
        updateSubscriberFirstname(target, source);
        updateSubscriberPatronymic(target, source);
        updateSubscriberLastname(target, source);
        updateSubscriberContractNumber(target, source);
        updateSubscriberAccountNumber(target, source);
        updateSubscriberHouse(target, source);
        updateSubscriberFlat(target, source);
        updateSubscriberPhoneNumber(target, source);
        updateSubscriberEmail(target, source);
        updateSubscriberActiveFlag(target, source);
        updateSubscriberConnectionDate(target, source);
    }

    private void updateSubscriberCity(Subscriber target, SubscriberDto source) {
        Long cityId = source.getCity().getId();
        if (cityId == null) {
            log.error("City id is not present to update subscriber");
            throw new ValidationException("City id is not present to update subscriber");
        }
        cityRepository.findById(cityId)
                .ifPresentOrElse(target::setCity, () -> {
                    log.error("City with id - {} not found to update subscriber", cityId);
                    throw new BusinessException(
                            String.format("City with id - %d not found to update subscriber", cityId));
                });
    }

    private void updateSubscriberStreet(Subscriber target, SubscriberDto source) {
        Long streetId = source.getStreet().getId();
        if (streetId == null) {
            log.error("Street id is not present to update subscriber");
            throw new ValidationException("Street id is not present to update subscriber");
        }
        streetRepository.findById(streetId)
                .ifPresentOrElse(target::setStreet, () -> {
                    log.error("Street with id - {} not found to update subscriber", streetId);
                    throw new BusinessException(
                            String.format("Street with id - %d not found to update subscriber", streetId));
                });
    }

    private void updateSubscriberFirstname(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getFirstname(), source.getFirstname())) {
            log.debug("Firstname of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getFirstname(), source.getFirstname());
            target.setFirstname(source.getFirstname());
        }
    }

    private void updateSubscriberPatronymic(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getPatronymic(), source.getPatronymic())) {
            log.debug("Patronymic of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getPatronymic(), source.getPatronymic());
            target.setPatronymic(source.getPatronymic());
        }
    }

    private void updateSubscriberLastname(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getLastname(), source.getLastname())) {
            log.debug("Lastname of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getLastname(), source.getLastname());
            target.setLastname(source.getLastname());
        }
    }

    private void updateSubscriberContractNumber(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getContractNumber(), source.getContractNumber())) {
            log.debug("ContractNumber of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getContractNumber(), source.getContractNumber());
            target.setContractNumber(source.getContractNumber());
        }
    }

    private void updateSubscriberAccountNumber(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getAccountNumber(), source.getAccountNumber())) {
            log.debug("AccountNumber of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getAccountNumber(), source.getAccountNumber());
            target.setAccountNumber(source.getAccountNumber());
        }
    }

    private void updateSubscriberHouse(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getHouse(), source.getHouse())) {
            log.debug("House of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getHouse(), source.getHouse());
            target.setHouse(source.getHouse());
        }
    }

    private void updateSubscriberFlat(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getFlat(), source.getFlat())) {
            log.debug("Flat of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getFlat(), source.getFlat());
            target.setFlat(source.getFlat());
        }
    }

    private void updateSubscriberPhoneNumber(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getPhoneNumber(), source.getPhoneNumber())) {
            log.debug("PhoneNumber of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getPhoneNumber(), source.getPhoneNumber());
            target.setPhoneNumber(source.getPhoneNumber());
        }
    }

    private void updateSubscriberEmail(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getEmail(), source.getEmail())) {
            log.debug("Email of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getEmail(), source.getEmail());
            target.setEmail(source.getEmail());
        }
    }

    private void updateSubscriberActiveFlag(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getIsActive(), source.getIsActive())) {
            log.debug("IsActive of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getIsActive(), source.getIsActive());
            target.setIsActive(source.getIsActive());
        }
    }

    private void updateSubscriberConnectionDate(Subscriber target, SubscriberDto source) {
        if (!Objects.equals(target.getConnectionDate(), source.getConnectionDate())) {
            log.debug("ConnectionDate of subscriber - {} was changed from {} to {}",
                    target.getId(), target.getConnectionDate(), source.getConnectionDate());
            target.setConnectionDate(source.getConnectionDate());
        }
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        subscriberRepository.deleteById(id);
    }

}
