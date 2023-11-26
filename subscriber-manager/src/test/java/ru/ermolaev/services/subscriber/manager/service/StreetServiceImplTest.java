package ru.ermolaev.services.subscriber.manager.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.ermolaev.services.subscriber.manager.domain.Street;
import ru.ermolaev.services.subscriber.manager.mapper.StreetMapperImpl;
import ru.ermolaev.services.subscriber.manager.repository.StreetRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.StreetDto;
import ru.ermolaev.services.subscriber.manager.service.impl.StreetServiceImpl;
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

@DisplayName("Сервис по работе с улицами ")
@SpringBootTest(classes = {StreetServiceImpl.class, StreetMapperImpl.class})
public class StreetServiceImplTest {

    private static final Long STREET_ID = 1L;

    @MockBean
    private StreetRepository streetRepository;

    @Autowired
    private StreetServiceImpl streetService;

    @Test
    @DisplayName("должен найти улицу по ID")
    public void shouldFindOneStreetById() {
        Street dummyStreet = DummyDataHelper.getDummyStreet();
        dummyStreet.setId(STREET_ID);
        when(streetRepository.findById(STREET_ID)).thenReturn(Optional.of(dummyStreet));

        StreetDto streetDto = streetService.findOneById(STREET_ID);

        assertThat(streetDto).isNotNull();
        assertThat(streetDto.getId()).isEqualTo(dummyStreet.getId());
        assertThat(streetDto.getName()).isEqualTo(dummyStreet.getName());

        verify(streetRepository, times(1)).findById(STREET_ID);
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке найти несуществующую улицу")
    public void shouldThrowExceptionForFindNotExistStreet() {
        when(streetRepository.findById(STREET_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> streetService.findOneById(STREET_ID))
                .isInstanceOf(EntityNotFoundException.class);

        verify(streetRepository, times(1)).findById(STREET_ID);
    }

    @Test
    @DisplayName("должен найти все улицы")
    public void shouldFindAllStreets() {
        when(streetRepository.findAll()).thenReturn(List.of(DummyDataHelper.getDummyStreet()));

        List<StreetDto> streetDtoList = streetService.findAll();

        assertThat(streetDtoList)
                .isNotNull()
                .hasSize(1);

        verify(streetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("должен создать новую улицу")
    public void shouldCreateNewStreet() {
        Street dummyStreet = DummyDataHelper.getDummyStreet();
        dummyStreet.setId(STREET_ID);
        StreetDto dummyStreetDto = DummyDataHelper.getDummyStreetDto();
        dummyStreetDto.setId(STREET_ID);

        when(streetRepository.save(any(Street.class))).thenReturn(dummyStreet);

        streetService.create(dummyStreetDto);

        assertThat(dummyStreet).isNotNull();

        verify(streetRepository, times(1)).save(any(Street.class));
    }

    @Test
    @DisplayName("должен обновить существующую улицу")
    public void shouldUpdateExistStreet() {
        Street dummyStreet = DummyDataHelper.getDummyStreet();
        dummyStreet.setId(STREET_ID);
        StreetDto dummyStreetDto = DummyDataHelper.getDummyStreetDto();
        dummyStreetDto.setId(STREET_ID);
        dummyStreetDto.setName(dummyStreet.getName() + "_upd");

        when(streetRepository.findById(STREET_ID)).thenReturn(Optional.of(dummyStreet));
        when(streetRepository.save(any(Street.class))).thenReturn(dummyStreet);

        StreetDto update = streetService.update(dummyStreetDto);

        assertThat(update).isNotNull();
        assertThat(update.getName()).isEqualTo(dummyStreetDto.getName());

        verify(streetRepository, times(1)).findById(STREET_ID);
        verify(streetRepository, times(1)).save(any(Street.class));
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке обновить несуществующую улицу")
    public void shouldThrowExceptionWhileUpdateNotExistStreet() {
        StreetDto dummyStreetDto = DummyDataHelper.getDummyStreetDto();
        dummyStreetDto.setId(STREET_ID);

        when(streetRepository.findById(STREET_ID)).thenReturn(Optional.empty());
        when(streetRepository.save(any())).thenReturn(null);

        assertThatThrownBy(() -> streetService.update(dummyStreetDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(streetRepository, times(1)).findById(STREET_ID);
        verify(streetRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("должен удалить существующую улицу")
    public void shouldDeleteExistStreetById() {
        doNothing().when(streetRepository).deleteById(STREET_ID);

        streetService.deleteById(STREET_ID);

        verify(streetRepository, times(1)).deleteById(STREET_ID);
    }

}
