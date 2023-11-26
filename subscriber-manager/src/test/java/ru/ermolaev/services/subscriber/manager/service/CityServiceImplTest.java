package ru.ermolaev.services.subscriber.manager.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.ermolaev.services.subscriber.manager.domain.City;
import ru.ermolaev.services.subscriber.manager.mapper.CityMapperImpl;
import ru.ermolaev.services.subscriber.manager.repository.CityRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.CityDto;
import ru.ermolaev.services.subscriber.manager.service.impl.CityServiceImpl;
import ru.ermolaev.services.subscriber.manager.util.DummyDataHelper;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Сервис по работе с городами ")
@SpringBootTest(classes = {CityServiceImpl.class, CityMapperImpl.class})
public class CityServiceImplTest {

    private static final Long CITY_ID = 1L;

    @MockBean
    private CityRepository cityRepository;

    @Autowired
    private CityServiceImpl cityService;

    @Test
    @DisplayName("должен найти город по ID")
    public void shouldFindOneCityById() {
        City dummyCity = DummyDataHelper.getDummyCity();
        dummyCity.setId(CITY_ID);
        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.of(dummyCity));

        CityDto cityDto = cityService.findOneById(CITY_ID);

        assertThat(cityDto).isNotNull();
        assertThat(cityDto.getId()).isEqualTo(dummyCity.getId());
        assertThat(cityDto.getName()).isEqualTo(dummyCity.getName());

        verify(cityRepository, times(1)).findById(CITY_ID);
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке найти несуществующий город")
    public void shouldThrowExceptionForFindNotExistCity() {
        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cityService.findOneById(CITY_ID))
                .isInstanceOf(EntityNotFoundException.class);

        verify(cityRepository, times(1)).findById(CITY_ID);
    }

    @Test
    @DisplayName("должен найти все города")
    public void shouldFindAllCities() {
        when(cityRepository.findAll()).thenReturn(List.of(DummyDataHelper.getDummyCity()));

        List<CityDto> cities = cityService.findAll();

        assertThat(cities)
                .isNotNull()
                .hasSize(1);

        verify(cityRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("должен создать новый город")
    public void shouldCreateNewCity() {
        City dummyCity = DummyDataHelper.getDummyCity();
        dummyCity.setId(CITY_ID);
        CityDto dummyCityDto = DummyDataHelper.getDummyCityDto();
        dummyCityDto.setId(CITY_ID);

        when(cityRepository.save(any(City.class))).thenReturn(dummyCity);

        cityService.create(dummyCityDto);

        assertThat(dummyCity).isNotNull();

        verify(cityRepository, times(1)).save(any(City.class));
    }

    @Test
    @DisplayName("должен обновить существующий город")
    public void shouldUpdateExistCity() {
        City dummyCity = DummyDataHelper.getDummyCity();
        dummyCity.setId(CITY_ID);
        CityDto dummyCityDto = DummyDataHelper.getDummyCityDto();
        dummyCityDto.setId(CITY_ID);
        dummyCityDto.setName(dummyCity.getName() + "_upd");

        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.of(dummyCity));
        when(cityRepository.save(any(City.class))).thenReturn(dummyCity);

        CityDto update = cityService.update(dummyCityDto);

        assertThat(update).isNotNull();
        assertThat(update.getName()).isEqualTo(dummyCityDto.getName());

        verify(cityRepository, times(1)).findById(CITY_ID);
        verify(cityRepository, times(1)).save(any(City.class));
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке обновить несуществующий город")
    public void shouldThrowExceptionWhileUpdateNotExistCity() {
        CityDto dummyCityDto = DummyDataHelper.getDummyCityDto();
        dummyCityDto.setId(CITY_ID);

        when(cityRepository.findById(CITY_ID)).thenReturn(Optional.empty());
        when(cityRepository.save(any())).thenReturn(null);

        assertThatThrownBy(() -> cityService.update(dummyCityDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(cityRepository, times(1)).findById(CITY_ID);
        verify(cityRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("должен удалить существующий город")
    public void shouldDeleteExistCityById() {
        doNothing().when(cityRepository).deleteById(CITY_ID);

        cityService.deleteById(CITY_ID);

        verify(cityRepository, times(1)).deleteById(CITY_ID);
    }

}
