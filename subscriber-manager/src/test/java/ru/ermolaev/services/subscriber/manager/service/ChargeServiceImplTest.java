package ru.ermolaev.services.subscriber.manager.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.ermolaev.services.subscriber.manager.domain.Charge;
import ru.ermolaev.services.subscriber.manager.domain.ChargeTarget;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.mapper.ChargeMapperImpl;
import ru.ermolaev.services.subscriber.manager.mapper.ChargeTargetMapperImpl;
import ru.ermolaev.services.subscriber.manager.repository.ChargeRepository;
import ru.ermolaev.services.subscriber.manager.repository.ChargeTargetRepository;
import ru.ermolaev.services.subscriber.manager.repository.SubscriberRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeDto;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeTargetDto;
import ru.ermolaev.services.subscriber.manager.service.impl.ChargeServiceImpl;
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

@DisplayName("Сервис по работе с начислениями ")
@SpringBootTest(classes = {ChargeServiceImpl.class, ChargeMapperImpl.class, ChargeTargetMapperImpl.class})
public class ChargeServiceImplTest {

    private static final Long CHARGE_ID = 1L;

    private static final Long SUBSCRIBER_ID = 1L;

    private static final Long CHARGE_TARGET_ID = 1L;

    private static final String UPDATE_SUFFIX = "_upd";

    @MockBean
    private ChargeRepository chargeRepository;

    @MockBean
    private ChargeTargetRepository chargeTargetRepository;

    @MockBean
    private SubscriberRepository subscriberRepository;

    @Autowired
    private ChargeServiceImpl chargeService;

    @Test
    @DisplayName("должен найти начисление по ID")
    public void shouldFindOneChargeById() {
        ChargeTarget dummyChargeTarget = DummyDataHelper.getDummyChargeTarget();
        dummyChargeTarget.setId(CHARGE_TARGET_ID);
        Subscriber dummySubscriber = DummyDataHelper.getDummySubscriber();
        dummySubscriber.setId(SUBSCRIBER_ID);

        Charge dummyCharge = DummyDataHelper.getDummyCharge();
        dummyCharge.setId(CHARGE_ID);
        dummyCharge.setChargeTarget(dummyChargeTarget);
        dummyCharge.setSubscriber(dummySubscriber);

        when(chargeRepository.findById(CHARGE_ID)).thenReturn(Optional.of(dummyCharge));

        ChargeDto chargeDto = chargeService.findOneById(CHARGE_ID);

        assertThat(chargeDto).isNotNull();
        assertThat(chargeDto.getId()).isEqualTo(dummyCharge.getId());
        assertThat(chargeDto.getChargeDate()).isEqualTo(dummyCharge.getChargeDate());
        assertThat(chargeDto.getAmount()).isEqualTo(dummyCharge.getAmount());
        assertThat(chargeDto.getPeriod()).isEqualTo(dummyCharge.getPeriod());
        assertThat(chargeDto.getComment()).isEqualTo(dummyCharge.getComment());
        assertThat(chargeDto.getChargeTarget().getId()).isEqualTo(dummyCharge.getChargeTarget().getId());
        assertThat(chargeDto.getSubscriberId()).isEqualTo(dummyCharge.getSubscriber().getId());

        verify(chargeRepository, times(1)).findById(CHARGE_ID);
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке найти несуществующее начисление")
    public void shouldThrowExceptionForFindNotExistCharge() {
        when(chargeRepository.findById(CHARGE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chargeService.findOneById(CHARGE_ID))
                .isInstanceOf(EntityNotFoundException.class);

        verify(chargeRepository, times(1)).findById(CHARGE_ID);
    }

    @Test
    @DisplayName("должен найти все начисления принадлежащие конкретному абоненту")
    public void shouldFindAllChargesBySubscriberId() {
        when(chargeRepository.findAllBySubscriberId(SUBSCRIBER_ID)).thenReturn(List.of(DummyDataHelper.getDummyCharge()));

        List<ChargeDto> chargeDtoList = chargeService.findAllBySubscriberId(SUBSCRIBER_ID);

        assertThat(chargeDtoList)
                .isNotNull()
                .hasSize(1);

        verify(chargeRepository, times(1)).findAllBySubscriberId(SUBSCRIBER_ID);
    }

    @Test
    @DisplayName("должен создать новое начисление")
    public void shouldCreateNewCharge() {
        Charge dummyCharge = DummyDataHelper.getDummyCharge();
        dummyCharge.setId(CHARGE_ID);
        ChargeTargetDto chargeTargetDto = DummyDataHelper.getDummyChargeTargetDto();
        chargeTargetDto.setId(CHARGE_TARGET_ID);

        ChargeDto dummyChargeDto = DummyDataHelper.getDummyChargeDto();
        dummyChargeDto.setId(CHARGE_ID);
        dummyChargeDto.setChargeTarget(chargeTargetDto);
        dummyChargeDto.setSubscriberId(SUBSCRIBER_ID);

        when(chargeTargetRepository.findById(any(Long.class))).thenReturn(Optional.of(DummyDataHelper.getDummyChargeTarget()));
        when(subscriberRepository.findById(any(Long.class))).thenReturn(Optional.of(DummyDataHelper.getDummySubscriber()));
        when(chargeRepository.save(any(Charge.class))).thenReturn(dummyCharge);

        chargeService.create(dummyChargeDto);

        assertThat(dummyCharge).isNotNull();

        verify(chargeTargetRepository, times(1)).findById(any(Long.class));
        verify(subscriberRepository, times(1)).findById(any(Long.class));
        verify(chargeRepository, times(1)).save(any(Charge.class));
    }

    @Test
    @DisplayName("должен обновить существующее начисление")
    public void shouldUpdateExistCharge() {
        Charge dummyCharge = DummyDataHelper.getDummyCharge();
        dummyCharge.setId(CHARGE_ID);

        ChargeTargetDto chargeTargetDto = DummyDataHelper.getDummyChargeTargetDto();
        chargeTargetDto.setId(CHARGE_TARGET_ID);

        ChargeDto dummyChargeDto = DummyDataHelper.getDummyChargeDto();
        dummyChargeDto.setId(CHARGE_ID);
        dummyChargeDto.setPeriod(dummyCharge.getPeriod() + UPDATE_SUFFIX);
        dummyChargeDto.setChargeDate(dummyCharge.getChargeDate().plusDays(1));
        dummyChargeDto.setComment(dummyCharge.getComment() + UPDATE_SUFFIX);
        dummyChargeDto.setAmount(dummyCharge.getAmount() + 1);
        dummyChargeDto.setChargeTarget(chargeTargetDto);
        dummyChargeDto.setSubscriberId(SUBSCRIBER_ID);

        when(chargeTargetRepository.findById(any(Long.class))).thenReturn(Optional.of(DummyDataHelper.getDummyChargeTarget()));
        when(subscriberRepository.findById(any(Long.class))).thenReturn(Optional.of(DummyDataHelper.getDummySubscriber()));
        when(chargeRepository.findById(CHARGE_ID)).thenReturn(Optional.of(dummyCharge));
        when(chargeRepository.save(any(Charge.class))).thenReturn(dummyCharge);

        ChargeDto update = chargeService.update(dummyChargeDto);

        assertThat(update).isNotNull();
        assertThat(update.getPeriod()).isEqualTo(dummyChargeDto.getPeriod());
        assertThat(update.getChargeDate()).isEqualTo(dummyChargeDto.getChargeDate());
        assertThat(update.getComment()).isEqualTo(dummyChargeDto.getComment());
        assertThat(update.getAmount()).isEqualTo(dummyChargeDto.getAmount());

        verify(chargeTargetRepository, times(1)).findById(any(Long.class));
        verify(subscriberRepository, times(1)).findById(any(Long.class));
        verify(chargeRepository, times(1)).findById(CHARGE_ID);
        verify(chargeRepository, times(1)).save(any(Charge.class));
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке обновить несуществующее начисление")
    public void shouldThrowExceptionWhileUpdateNotExistCharge() {
        ChargeDto dummyChargeDto = DummyDataHelper.getDummyChargeDto();
        dummyChargeDto.setId(CHARGE_ID);

        when(chargeRepository.findById(CHARGE_ID)).thenReturn(Optional.empty());
        when(chargeRepository.save(any())).thenReturn(null);

        assertThatThrownBy(() -> chargeService.update(dummyChargeDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(chargeRepository, times(1)).findById(CHARGE_ID);
        verify(chargeRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("должен удалить существующее начисление")
    public void shouldDeleteExistChargeById() {
        doNothing().when(chargeRepository).deleteById(CHARGE_ID);

        chargeService.deleteById(CHARGE_ID);

        verify(chargeRepository, times(1)).deleteById(CHARGE_ID);
    }

}
