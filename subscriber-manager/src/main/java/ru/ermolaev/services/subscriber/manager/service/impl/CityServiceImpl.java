package ru.ermolaev.services.subscriber.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ermolaev.services.subscriber.manager.domain.City;
import ru.ermolaev.services.subscriber.manager.mapper.CityMapper;
import ru.ermolaev.services.subscriber.manager.repository.CityRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.CityDto;
import ru.ermolaev.services.subscriber.manager.service.CityService;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;

    private final CityMapper cityMapper;

    @Override
    @Transactional(readOnly = true)
    public CityDto findOneById(long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("City with id - {} not found", id);
                    return new EntityNotFoundException(String.format("City with id - %d not found", id));
                });
        return cityMapper.toDto(city);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CityDto> findAll() {
        List<City> cities = cityRepository.findAll();
        return cityMapper.toDtoList(cities);
    }

    @Override
    @Transactional
    public CityDto create(CityDto dto) {
        City newCity = cityMapper.toDomain(dto);
        cityRepository.save(newCity);
        log.info("City: {} was created", newCity);
        return cityMapper.toDto(newCity);
    }

    @Override
    @Transactional
    public CityDto update(CityDto dto) {
        City existCity = cityRepository.findById(dto.getId()).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("City with id - %d not found", dto.getId())));

        if (!Objects.equals(existCity.getName(), dto.getName())) {
            log.debug("Name of city with id - {} was changed from {} to {}",
                    existCity.getId(), existCity.getName(), dto.getName());
            existCity.setName(dto.getName());
        }

        cityRepository.save(existCity);
        log.info("City: {} was updated", existCity);
        return cityMapper.toDto(existCity);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        cityRepository.deleteById(id);
        log.info("City with id: {} was deleted", id);
    }
}
