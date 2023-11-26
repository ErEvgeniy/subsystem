package ru.ermolaev.services.subscriber.manager.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.ermolaev.services.subscriber.manager.domain.City;
import ru.ermolaev.services.subscriber.manager.domain.Street;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.exception.ValidationException;
import ru.ermolaev.services.subscriber.manager.mapper.CityMapperImpl;
import ru.ermolaev.services.subscriber.manager.mapper.StreetMapperImpl;
import ru.ermolaev.services.subscriber.manager.mapper.SubscriberMapperImpl;
import ru.ermolaev.services.subscriber.manager.repository.CityRepository;
import ru.ermolaev.services.subscriber.manager.repository.StreetRepository;
import ru.ermolaev.services.subscriber.manager.repository.SubscriberRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.CityDto;
import ru.ermolaev.services.subscriber.manager.rest.dto.StreetDto;
import ru.ermolaev.services.subscriber.manager.rest.dto.SubscriberDto;
import ru.ermolaev.services.subscriber.manager.service.impl.SubscriberServiceImpl;
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

@DisplayName("Сервис по работе с абонентами ")
@SpringBootTest(classes = {
        SubscriberServiceImpl.class,
        SubscriberMapperImpl.class,
        CityMapperImpl.class,
        StreetMapperImpl.class
})
public class SubscriberServiceImplTest {

    private static final Long SUBSCRIBER_ID = 1L;

    private static final Long CITY_ID = 1L;

    private static final Long STREET_ID = 1L;

    private static final String UPDATE_SUFFIX = "_upd";

    @MockBean
    private SubscriberRepository subscriberRepository;

    @MockBean
    private CityRepository cityRepository;

    @MockBean
    private StreetRepository streetRepository;

    @Autowired
    private SubscriberServiceImpl subscriberService;

    @Test
    @DisplayName("должен найти абонента по ID")
    public void shouldFindOneSubscriberById() {
        City dummyCity = DummyDataHelper.getDummyCity();
        dummyCity.setId(CITY_ID);
        Street dummyStreet = DummyDataHelper.getDummyStreet();
        dummyStreet.setId(STREET_ID);

        Subscriber dummySubscriber = DummyDataHelper.getDummySubscriber();
        dummySubscriber.setId(SUBSCRIBER_ID);
        dummySubscriber.setCity(dummyCity);
        dummySubscriber.setStreet(dummyStreet);

        when(subscriberRepository.findById(SUBSCRIBER_ID)).thenReturn(Optional.of(dummySubscriber));

        SubscriberDto subscriberDto = subscriberService.findOneById(SUBSCRIBER_ID);

        assertThat(subscriberDto).isNotNull();
        assertThat(subscriberDto.getId()).isEqualTo(dummySubscriber.getId());
        assertThat(subscriberDto.getFirstname()).isEqualTo(dummySubscriber.getFirstname());
        assertThat(subscriberDto.getPatronymic()).isEqualTo(dummySubscriber.getPatronymic());
        assertThat(subscriberDto.getLastname()).isEqualTo(dummySubscriber.getLastname());
        assertThat(subscriberDto.getAccountNumber()).isEqualTo(dummySubscriber.getAccountNumber());
        assertThat(subscriberDto.getContractNumber()).isEqualTo(dummySubscriber.getContractNumber());
        assertThat(subscriberDto.getEmail()).isEqualTo(dummySubscriber.getEmail());
        assertThat(subscriberDto.getPhoneNumber()).isEqualTo(dummySubscriber.getPhoneNumber());
        assertThat(subscriberDto.getIsActive()).isEqualTo(dummySubscriber.getIsActive());
        assertThat(subscriberDto.getConnectionDate()).isEqualTo(dummySubscriber.getConnectionDate());
        assertThat(subscriberDto.getHouse()).isEqualTo(dummySubscriber.getHouse());
        assertThat(subscriberDto.getFlat()).isEqualTo(dummySubscriber.getFlat());
        assertThat(subscriberDto.getCity().getId()).isEqualTo(dummySubscriber.getCity().getId());
        assertThat(subscriberDto.getStreet().getId()).isEqualTo(dummySubscriber.getStreet().getId());

        verify(subscriberRepository, times(1)).findById(SUBSCRIBER_ID);
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке найти несуществующего абонента")
    public void shouldThrowExceptionForFindNotExistSubscriber() {
        when(subscriberRepository.findById(SUBSCRIBER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriberService.findOneById(SUBSCRIBER_ID))
                .isInstanceOf(EntityNotFoundException.class);

        verify(subscriberRepository, times(1)).findById(SUBSCRIBER_ID);
    }

    @Test
    @DisplayName("должен найти всех абонентов")
    public void shouldFindAllSubscribers() {
        when(subscriberRepository.findAll()).thenReturn(List.of(DummyDataHelper.getDummySubscriber()));

        List<SubscriberDto> subscriberDtoList = subscriberService.findAll();

        assertThat(subscriberDtoList)
                .isNotNull()
                .hasSize(1);

        verify(subscriberRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("должен создать нового абонента")
    public void shouldCreateNewSubscriber() {
        CityDto dummyCityDto = DummyDataHelper.getDummyCityDto();
        dummyCityDto.setId(CITY_ID);
        StreetDto dummyStreetDto = DummyDataHelper.getDummyStreetDto();
        dummyStreetDto.setId(STREET_ID);

        SubscriberDto dummySubscriberDto = DummyDataHelper.getDummySubscriberDto();
        dummySubscriberDto.setId(SUBSCRIBER_ID);
        dummySubscriberDto.setCity(dummyCityDto);
        dummySubscriberDto.setStreet(dummyStreetDto);

        when(cityRepository.findById(any(Long.class))).thenReturn(Optional.of(DummyDataHelper.getDummyCity()));
        when(streetRepository.findById(any(Long.class))).thenReturn(Optional.of(DummyDataHelper.getDummyStreet()));
        when(subscriberRepository.save(any(Subscriber.class))).thenReturn(DummyDataHelper.getDummySubscriber());

        subscriberService.create(dummySubscriberDto);

        assertThat(dummyCityDto).isNotNull();

        verify(cityRepository, times(1)).findById(any(Long.class));
        verify(streetRepository, times(1)).findById(any(Long.class));
        verify(subscriberRepository, times(1)).save(any(Subscriber.class));
    }

    @Test
    @DisplayName("должен обновить существующего абонента")
    public void shouldUpdateExistSubscriber() {
        Subscriber dummySubscriber = DummyDataHelper.getDummySubscriber();
        dummySubscriber.setId(SUBSCRIBER_ID);

        CityDto dummyCityDto = DummyDataHelper.getDummyCityDto();
        dummyCityDto.setId(CITY_ID);
        StreetDto dummyStreetDto = DummyDataHelper.getDummyStreetDto();
        dummyStreetDto.setId(STREET_ID);

        City dummyCity = DummyDataHelper.getDummyCity();
        dummyCity.setId(CITY_ID);
        Street dummyStreet = DummyDataHelper.getDummyStreet();
        dummyStreet.setId(STREET_ID);

        SubscriberDto dummySubscriberDto = DummyDataHelper.getDummySubscriberDto();
        dummySubscriberDto.setId(CITY_ID);
        dummySubscriberDto.setFirstname(dummySubscriber.getFirstname() + UPDATE_SUFFIX);
        dummySubscriberDto.setPatronymic(dummySubscriber.getPatronymic() + UPDATE_SUFFIX);
        dummySubscriberDto.setLastname(dummySubscriber.getLastname() + UPDATE_SUFFIX);
        dummySubscriberDto.setContractNumber(dummySubscriber.getContractNumber() + UPDATE_SUFFIX);
        dummySubscriberDto.setAccountNumber(dummySubscriber.getAccountNumber() + UPDATE_SUFFIX);
        dummySubscriberDto.setHouse(dummySubscriber.getHouse() + 1);
        dummySubscriberDto.setBalance(dummySubscriber.getBalance());
        dummySubscriberDto.setFlat(dummySubscriber.getFlat() + 1);
        dummySubscriberDto.setCity(dummyCityDto);
        dummySubscriberDto.setStreet(dummyStreetDto);
        dummySubscriberDto.setEmail(dummySubscriber.getEmail() + UPDATE_SUFFIX);
        dummySubscriberDto.setPhoneNumber(dummySubscriber.getPhoneNumber() + UPDATE_SUFFIX);
        dummySubscriberDto.setConnectionDate(dummySubscriber.getConnectionDate().plusDays(1));
        dummySubscriberDto.setIsActive(!dummySubscriber.getIsActive());

        when(cityRepository.findById(any(Long.class))).thenReturn(Optional.of(dummyCity));
        when(streetRepository.findById(any(Long.class))).thenReturn(Optional.of(dummyStreet));
        when(subscriberRepository.findById(SUBSCRIBER_ID)).thenReturn(Optional.of(dummySubscriber));
        when(subscriberRepository.save(any(Subscriber.class))).thenReturn(dummySubscriber);

        SubscriberDto update = subscriberService.update(dummySubscriberDto);

        assertThat(update).isNotNull();
        assertThat(update.getFirstname()).isEqualTo(dummySubscriberDto.getFirstname());
        assertThat(update.getPatronymic()).isEqualTo(dummySubscriberDto.getPatronymic());
        assertThat(update.getLastname()).isEqualTo(dummySubscriberDto.getLastname());
        assertThat(update.getContractNumber()).isEqualTo(dummySubscriberDto.getContractNumber());
        assertThat(update.getAccountNumber()).isEqualTo(dummySubscriberDto.getAccountNumber());
        assertThat(update.getHouse()).isEqualTo(dummySubscriberDto.getHouse());
        assertThat(update.getFlat()).isEqualTo(dummySubscriberDto.getFlat());
        assertThat(update.getEmail()).isEqualTo(dummySubscriberDto.getEmail());
        assertThat(update.getPhoneNumber()).isEqualTo(dummySubscriberDto.getPhoneNumber());
        assertThat(update.getConnectionDate()).isEqualTo(dummySubscriberDto.getConnectionDate());
        assertThat(update.getIsActive()).isEqualTo(dummySubscriberDto.getIsActive());
        assertThat(update.getCity().getId()).isEqualTo(dummySubscriberDto.getCity().getId());
        assertThat(update.getStreet().getId()).isEqualTo(dummySubscriberDto.getStreet().getId());

        verify(cityRepository, times(1)).findById(any(Long.class));
        verify(streetRepository, times(1)).findById(any(Long.class));
        verify(subscriberRepository, times(1)).findById(SUBSCRIBER_ID);
        verify(subscriberRepository, times(1)).save(any(Subscriber.class));
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке обновить баланс абонента")
    public void shouldThrowExceptionWhileUpdateSubscriberBalance() {
        Subscriber dummySubscriber = DummyDataHelper.getDummySubscriber();
        dummySubscriber.setId(SUBSCRIBER_ID);

        CityDto dummyCityDto = DummyDataHelper.getDummyCityDto();
        dummyCityDto.setId(CITY_ID);
        StreetDto dummyStreetDto = DummyDataHelper.getDummyStreetDto();
        dummyStreetDto.setId(STREET_ID);

        City dummyCity = DummyDataHelper.getDummyCity();
        dummyCity.setId(CITY_ID);
        Street dummyStreet = DummyDataHelper.getDummyStreet();
        dummyStreet.setId(STREET_ID);

        SubscriberDto dummySubscriberDto = DummyDataHelper.getDummySubscriberDto();
        dummySubscriberDto.setId(CITY_ID);
        dummySubscriberDto.setBalance(999F);

        when(cityRepository.findById(any(Long.class))).thenReturn(Optional.of(dummyCity));
        when(streetRepository.findById(any(Long.class))).thenReturn(Optional.of(dummyStreet));
        when(subscriberRepository.findById(SUBSCRIBER_ID)).thenReturn(Optional.of(dummySubscriber));
        when(subscriberRepository.save(any(Subscriber.class))).thenReturn(dummySubscriber);

        assertThatThrownBy(() -> subscriberService.update(dummySubscriberDto))
                .isInstanceOf(ValidationException.class);

        verify(cityRepository, times(0)).findById(any(Long.class));
        verify(streetRepository, times(0)).findById(any(Long.class));
        verify(subscriberRepository, times(1)).findById(SUBSCRIBER_ID);
        verify(subscriberRepository, times(0)).save(any(Subscriber.class));
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке обновить несуществующего абонента")
    public void shouldThrowExceptionWhileUpdateNotExistSubscriber() {
        SubscriberDto dummySubscriberDto = DummyDataHelper.getDummySubscriberDto();
        dummySubscriberDto.setId(SUBSCRIBER_ID);

        when(subscriberRepository.findById(SUBSCRIBER_ID)).thenReturn(Optional.empty());
        when(subscriberRepository.save(any())).thenReturn(null);

        assertThatThrownBy(() -> subscriberService.update(dummySubscriberDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(subscriberRepository, times(1)).findById(SUBSCRIBER_ID);
        verify(subscriberRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("должен удалить существующего абонента")
    public void shouldDeleteExistSubscriberById() {
        doNothing().when(subscriberRepository).deleteById(SUBSCRIBER_ID);

        subscriberService.deleteById(SUBSCRIBER_ID);

        verify(subscriberRepository, times(1)).deleteById(SUBSCRIBER_ID);
    }

}
