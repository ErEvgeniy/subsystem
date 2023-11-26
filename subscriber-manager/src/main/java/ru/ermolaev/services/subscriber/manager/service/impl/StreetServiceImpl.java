package ru.ermolaev.services.subscriber.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ermolaev.services.subscriber.manager.domain.Street;
import ru.ermolaev.services.subscriber.manager.mapper.StreetMapper;
import ru.ermolaev.services.subscriber.manager.repository.StreetRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.StreetDto;
import ru.ermolaev.services.subscriber.manager.service.StreetService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreetServiceImpl implements StreetService {

    private final StreetRepository streetRepository;

    private final StreetMapper streetMapper;

    @Override
    @Transactional(readOnly = true)
    public StreetDto findOneById(long id) {
        Street street = streetRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Street with id - {} not found", id);
                    return new EntityNotFoundException(String.format("Street with id - %d not found", id));
                });
        return streetMapper.toDto(street);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StreetDto> findAll() {
        List<Street> streets = streetRepository.findAll();
        return streetMapper.toDtoList(streets);
    }

    @Override
    @Transactional
    public StreetDto create(StreetDto dto) {
        Street newStreet = streetMapper.toDomain(dto);
        streetRepository.save(newStreet);
        log.info("Street: {} was created", newStreet);
        return streetMapper.toDto(newStreet);
    }

    @Override
    @Transactional
    public StreetDto update(StreetDto dto) {
        Street existStreet = streetRepository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Street with id - %d not found", dto.getId())));

        if (!Objects.equals(existStreet.getName(), dto.getName())) {
            log.debug("Name of street with id - {} was changed from {} to {}",
                    existStreet.getId(), existStreet.getName(), dto.getName());
            existStreet.setName(dto.getName());
        }

        streetRepository.save(existStreet);
        log.info("Street: {} was updated", existStreet);
        return streetMapper.toDto(existStreet);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        streetRepository.deleteById(id);
        log.info("Street with id: {} was deleted", id);
    }

}
