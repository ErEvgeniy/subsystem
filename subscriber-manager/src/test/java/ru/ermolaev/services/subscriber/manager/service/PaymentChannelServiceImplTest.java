package ru.ermolaev.services.subscriber.manager.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.ermolaev.services.subscriber.manager.domain.PaymentChannel;
import ru.ermolaev.services.subscriber.manager.mapper.PaymentChannelMapperImpl;
import ru.ermolaev.services.subscriber.manager.repository.PaymentChannelRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentChannelDto;
import ru.ermolaev.services.subscriber.manager.service.impl.PaymentChannelServiceImpl;
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

@DisplayName("Сервис по работе с каналами платежей ")
@SpringBootTest(classes = {PaymentChannelServiceImpl.class, PaymentChannelMapperImpl.class})
public class PaymentChannelServiceImplTest {

    private static final Long PAYMENT_CHANNEL_ID = 1L;

    @MockBean
    private PaymentChannelRepository paymentChannelRepository;

    @Autowired
    private PaymentChannelServiceImpl paymentChannelService;

    @Test
    @DisplayName("должен найти канал платежа по ID")
    public void shouldFindOnePaymentChannelById() {
        PaymentChannel dummyPaymentChannel = DummyDataHelper.getDummyPaymentChannel();
        dummyPaymentChannel.setId(PAYMENT_CHANNEL_ID);
        when(paymentChannelRepository.findById(PAYMENT_CHANNEL_ID)).thenReturn(Optional.of(dummyPaymentChannel));

        PaymentChannelDto paymentChannelDto = paymentChannelService.findOneById(PAYMENT_CHANNEL_ID);

        assertThat(paymentChannelDto).isNotNull();
        assertThat(paymentChannelDto.getId()).isEqualTo(dummyPaymentChannel.getId());
        assertThat(paymentChannelDto.getName()).isEqualTo(dummyPaymentChannel.getName());

        verify(paymentChannelRepository, times(1)).findById(PAYMENT_CHANNEL_ID);
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке найти несуществующий канал платежа")
    public void shouldThrowExceptionForFindNotExistPaymentChannel() {
        when(paymentChannelRepository.findById(PAYMENT_CHANNEL_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentChannelService.findOneById(PAYMENT_CHANNEL_ID))
                .isInstanceOf(EntityNotFoundException.class);

        verify(paymentChannelRepository, times(1)).findById(PAYMENT_CHANNEL_ID);
    }

    @Test
    @DisplayName("должен найти все каналы платежа")
    public void shouldFindAllPaymentChannels() {
        when(paymentChannelRepository.findAll()).thenReturn(List.of(DummyDataHelper.getDummyPaymentChannel()));

        List<PaymentChannelDto> paymentChannelDtoList = paymentChannelService.findAll();

        assertThat(paymentChannelDtoList)
                .isNotNull()
                .hasSize(1);

        verify(paymentChannelRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("должен создать новый канал платежа")
    public void shouldCreateNewPaymentChannel() {
        PaymentChannel dummyPaymentChannel = DummyDataHelper.getDummyPaymentChannel();
        dummyPaymentChannel.setId(PAYMENT_CHANNEL_ID);
        PaymentChannelDto dummyPaymentChannelDto = DummyDataHelper.getDummyPaymentChannelDto();
        dummyPaymentChannelDto.setId(PAYMENT_CHANNEL_ID);

        when(paymentChannelRepository.save(any(PaymentChannel.class))).thenReturn(dummyPaymentChannel);

        paymentChannelService.create(dummyPaymentChannelDto);

        assertThat(dummyPaymentChannel).isNotNull();

        verify(paymentChannelRepository, times(1)).save(any(PaymentChannel.class));
    }

    @Test
    @DisplayName("должен обновить существующий канал платежа")
    public void shouldUpdateExistPaymentChannel() {
        PaymentChannel dummyPaymentChannel = DummyDataHelper.getDummyPaymentChannel();
        dummyPaymentChannel.setId(PAYMENT_CHANNEL_ID);
        PaymentChannelDto dummyPaymentChannelDto = DummyDataHelper.getDummyPaymentChannelDto();
        dummyPaymentChannelDto.setId(PAYMENT_CHANNEL_ID);
        dummyPaymentChannelDto.setName(dummyPaymentChannel.getName() + "_upd");

        when(paymentChannelRepository.findById(PAYMENT_CHANNEL_ID)).thenReturn(Optional.of(dummyPaymentChannel));
        when(paymentChannelRepository.save(any(PaymentChannel.class))).thenReturn(dummyPaymentChannel);

        PaymentChannelDto update = paymentChannelService.update(dummyPaymentChannelDto);

        assertThat(update).isNotNull();
        assertThat(update.getName()).isEqualTo(dummyPaymentChannelDto.getName());

        verify(paymentChannelRepository, times(1)).findById(PAYMENT_CHANNEL_ID);
        verify(paymentChannelRepository, times(1)).save(any(PaymentChannel.class));
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке обновить несуществующий канал платежа")
    public void shouldThrowExceptionWhileUpdateNotExistPaymentChannel() {
        PaymentChannelDto dummyPaymentChannelDto = DummyDataHelper.getDummyPaymentChannelDto();
        dummyPaymentChannelDto.setId(PAYMENT_CHANNEL_ID);

        when(paymentChannelRepository.findById(PAYMENT_CHANNEL_ID)).thenReturn(Optional.empty());
        when(paymentChannelRepository.save(any())).thenReturn(null);

        assertThatThrownBy(() -> paymentChannelService.update(dummyPaymentChannelDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(paymentChannelRepository, times(1)).findById(PAYMENT_CHANNEL_ID);
        verify(paymentChannelRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("должен удалить существующий канал платежа")
    public void shouldDeleteExistPaymentChannelById() {
        doNothing().when(paymentChannelRepository).deleteById(PAYMENT_CHANNEL_ID);

        paymentChannelService.deleteById(PAYMENT_CHANNEL_ID);

        verify(paymentChannelRepository, times(1)).deleteById(PAYMENT_CHANNEL_ID);
    }

}
