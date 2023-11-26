package ru.ermolaev.services.subscriber.manager.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.ermolaev.services.subscriber.manager.domain.ChargeTarget;
import ru.ermolaev.services.subscriber.manager.mapper.ChargeTargetMapperImpl;
import ru.ermolaev.services.subscriber.manager.repository.ChargeTargetRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeTargetDto;
import ru.ermolaev.services.subscriber.manager.service.impl.ChargeTargetServiceImpl;
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

@DisplayName("Сервис по работе с целями списаний ")
@SpringBootTest(classes = {ChargeTargetServiceImpl.class, ChargeTargetMapperImpl.class})
public class ChargeTargetServiceImplTest {

    private static final Long CHARGE_TARGET_ID = 1L;

    @MockBean
    private ChargeTargetRepository chargeTargetRepository;

    @Autowired
    private ChargeTargetServiceImpl chargeTargetService;

    @Test
    @DisplayName("должен найти цель списания по ID")
    public void shouldFindOneChargeTargetById() {
        ChargeTarget dummyChargeTarget = DummyDataHelper.getDummyChargeTarget();
        dummyChargeTarget.setId(CHARGE_TARGET_ID);
        when(chargeTargetRepository.findById(CHARGE_TARGET_ID)).thenReturn(Optional.of(dummyChargeTarget));

        ChargeTargetDto chargeTargetDto = chargeTargetService.findOneById(CHARGE_TARGET_ID);

        assertThat(chargeTargetDto).isNotNull();
        assertThat(chargeTargetDto.getId()).isEqualTo(dummyChargeTarget.getId());
        assertThat(chargeTargetDto.getName()).isEqualTo(dummyChargeTarget.getName());

        verify(chargeTargetRepository, times(1)).findById(CHARGE_TARGET_ID);
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке найти несуществующую цель списания")
    public void shouldThrowExceptionForFindNotExistChargeTarget() {
        when(chargeTargetRepository.findById(CHARGE_TARGET_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> chargeTargetService.findOneById(CHARGE_TARGET_ID))
                .isInstanceOf(EntityNotFoundException.class);

        verify(chargeTargetRepository, times(1)).findById(CHARGE_TARGET_ID);
    }

    @Test
    @DisplayName("должен найти все цели списания")
    public void shouldFindAllChargeTargets() {
        when(chargeTargetRepository.findAll()).thenReturn(List.of(DummyDataHelper.getDummyChargeTarget()));

        List<ChargeTargetDto> chargeTargetDtos = chargeTargetService.findAll();

        assertThat(chargeTargetDtos)
                .isNotNull()
                .hasSize(1);

        verify(chargeTargetRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("должен создать новую цель списания")
    public void shouldCreateNewChargeTarget() {
        ChargeTarget dummyChargeTarget = DummyDataHelper.getDummyChargeTarget();
        dummyChargeTarget.setId(CHARGE_TARGET_ID);
        ChargeTargetDto dummyChargeTargetDto = DummyDataHelper.getDummyChargeTargetDto();
        dummyChargeTargetDto.setId(CHARGE_TARGET_ID);

        when(chargeTargetRepository.save(any(ChargeTarget.class))).thenReturn(dummyChargeTarget);

        chargeTargetService.create(dummyChargeTargetDto);

        assertThat(dummyChargeTarget).isNotNull();

        verify(chargeTargetRepository, times(1)).save(any(ChargeTarget.class));
    }

    @Test
    @DisplayName("должен обновить существующую цель списания")
    public void shouldUpdateExistChargeTarget() {
        ChargeTarget dummyChargeTarget = DummyDataHelper.getDummyChargeTarget();
        dummyChargeTarget.setId(CHARGE_TARGET_ID);
        ChargeTargetDto dummyChargeTargetDto = DummyDataHelper.getDummyChargeTargetDto();
        dummyChargeTargetDto.setId(CHARGE_TARGET_ID);
        dummyChargeTargetDto.setName(dummyChargeTarget.getName() + "_upd");

        when(chargeTargetRepository.findById(CHARGE_TARGET_ID)).thenReturn(Optional.of(dummyChargeTarget));
        when(chargeTargetRepository.save(any(ChargeTarget.class))).thenReturn(dummyChargeTarget);

        ChargeTargetDto update = chargeTargetService.update(dummyChargeTargetDto);

        assertThat(update).isNotNull();
        assertThat(update.getName()).isEqualTo(dummyChargeTargetDto.getName());

        verify(chargeTargetRepository, times(1)).findById(CHARGE_TARGET_ID);
        verify(chargeTargetRepository, times(1)).save(any(ChargeTarget.class));
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке обновить несуществующую цель списания")
    public void shouldThrowExceptionWhileUpdateNotExistChargeTarget() {
        ChargeTargetDto dummyChargeTargetDto = DummyDataHelper.getDummyChargeTargetDto();
        dummyChargeTargetDto.setId(CHARGE_TARGET_ID);

        when(chargeTargetRepository.findById(CHARGE_TARGET_ID)).thenReturn(Optional.empty());
        when(chargeTargetRepository.save(any())).thenReturn(null);

        assertThatThrownBy(() -> chargeTargetService.update(dummyChargeTargetDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(chargeTargetRepository, times(1)).findById(CHARGE_TARGET_ID);
        verify(chargeTargetRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("должен удалить существующую цель списания")
    public void shouldDeleteExistChargeTargetById() {
        doNothing().when(chargeTargetRepository).deleteById(CHARGE_TARGET_ID);

        chargeTargetService.deleteById(CHARGE_TARGET_ID);

        verify(chargeTargetRepository, times(1)).deleteById(CHARGE_TARGET_ID);
    }

}
