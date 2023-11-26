package ru.ermolaev.services.subscriber.manager.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.ermolaev.services.subscriber.manager.domain.Payment;
import ru.ermolaev.services.subscriber.manager.domain.PaymentChannel;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.mapper.PaymentChannelMapperImpl;
import ru.ermolaev.services.subscriber.manager.mapper.PaymentMapperImpl;
import ru.ermolaev.services.subscriber.manager.repository.PaymentChannelRepository;
import ru.ermolaev.services.subscriber.manager.repository.PaymentRepository;
import ru.ermolaev.services.subscriber.manager.repository.SubscriberRepository;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentChannelDto;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentDto;
import ru.ermolaev.services.subscriber.manager.service.impl.PaymentServiceImpl;
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

@DisplayName("Сервис по работе с платежами ")
@SpringBootTest(classes = {PaymentServiceImpl.class, PaymentMapperImpl.class, PaymentChannelMapperImpl.class})
public class PaymentServiceImplTest {

    private static final Long PAYMENT_ID = 1L;

    private static final Long SUBSCRIBER_ID = 1L;

    private static final Long PAYMENT_CHANNEL_ID = 1L;

    private static final String UPDATE_SUFFIX = "_upd";

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private PaymentChannelRepository paymentChannelRepository;

    @MockBean
    private SubscriberRepository subscriberRepository;

    @Autowired
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("должен найти платеж по ID")
    public void shouldFindOnePaymentById() {
        PaymentChannel dummyPaymentChannel = DummyDataHelper.getDummyPaymentChannel();
        dummyPaymentChannel.setId(PAYMENT_CHANNEL_ID);
        Subscriber dummySubscriber = DummyDataHelper.getDummySubscriber();
        dummySubscriber.setId(SUBSCRIBER_ID);

        Payment dummyPayment = DummyDataHelper.getDummyPayment();
        dummyPayment.setId(PAYMENT_ID);
        dummyPayment.setPaymentChannel(dummyPaymentChannel);
        dummyPayment.setSubscriber(dummySubscriber);

        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(dummyPayment));

        PaymentDto paymentDto = paymentService.findOneById(PAYMENT_ID);

        assertThat(paymentDto).isNotNull();
        assertThat(paymentDto.getId()).isEqualTo(dummyPayment.getId());
        assertThat(paymentDto.getPaymentDate()).isEqualTo(dummyPayment.getPaymentDate());
        assertThat(paymentDto.getAmount()).isEqualTo(dummyPayment.getAmount());
        assertThat(paymentDto.getPeriod()).isEqualTo(dummyPayment.getPeriod());
        assertThat(paymentDto.getComment()).isEqualTo(dummyPayment.getComment());
        assertThat(paymentDto.getPaymentChannel().getId()).isEqualTo(dummyPayment.getPaymentChannel().getId());
        assertThat(paymentDto.getSubscriberId()).isEqualTo(dummyPayment.getSubscriber().getId());

        verify(paymentRepository, times(1)).findById(PAYMENT_ID);
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке найти несуществующий платеж")
    public void shouldThrowExceptionForFindNotExistPayment() {
        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.findOneById(PAYMENT_ID))
                .isInstanceOf(EntityNotFoundException.class);

        verify(paymentRepository, times(1)).findById(PAYMENT_ID);
    }

    @Test
    @DisplayName("должен найти все платежи принадлежащие конкретному абоненту")
    public void shouldFindAllPaymentsBySubscriberId() {
        when(paymentRepository.findAllBySubscriberId(SUBSCRIBER_ID)).thenReturn(List.of(DummyDataHelper.getDummyPayment()));

        List<PaymentDto> paymentDtoList = paymentService.findAllBySubscriberId(SUBSCRIBER_ID);

        assertThat(paymentDtoList)
                .isNotNull()
                .hasSize(1);

        verify(paymentRepository, times(1)).findAllBySubscriberId(SUBSCRIBER_ID);
    }

    @Test
    @DisplayName("должен создать новый платеж")
    public void shouldCreateNewPayment() {
        Payment dummyPayment = DummyDataHelper.getDummyPayment();
        dummyPayment.setId(PAYMENT_ID);
        PaymentChannelDto paymentChannelDto = DummyDataHelper.getDummyPaymentChannelDto();
        paymentChannelDto.setId(PAYMENT_CHANNEL_ID);

        PaymentDto dummyPaymentDto = DummyDataHelper.getDummyPaymentDto();
        dummyPaymentDto.setId(PAYMENT_ID);
        dummyPaymentDto.setPaymentChannel(paymentChannelDto);
        dummyPaymentDto.setSubscriberId(SUBSCRIBER_ID);

        when(paymentChannelRepository.findById(any(Long.class))).thenReturn(Optional.of(DummyDataHelper.getDummyPaymentChannel()));
        when(subscriberRepository.findById(any(Long.class))).thenReturn(Optional.of(DummyDataHelper.getDummySubscriber()));
        when(paymentRepository.save(any(Payment.class))).thenReturn(dummyPayment);

        paymentService.create(dummyPaymentDto);

        assertThat(dummyPayment).isNotNull();

        verify(paymentChannelRepository, times(1)).findById(any(Long.class));
        verify(subscriberRepository, times(1)).findById(any(Long.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("должен обновить существующий платеж")
    public void shouldUpdateExistPayment() {
        Payment dummyPayment = DummyDataHelper.getDummyPayment();
        dummyPayment.setId(PAYMENT_ID);

        PaymentChannelDto paymentChannelDto = DummyDataHelper.getDummyPaymentChannelDto();
        paymentChannelDto.setId(PAYMENT_CHANNEL_ID);

        PaymentDto dummyPaymentDto = DummyDataHelper.getDummyPaymentDto();
        dummyPaymentDto.setId(PAYMENT_ID);
        dummyPaymentDto.setPeriod(dummyPayment.getPeriod() + UPDATE_SUFFIX);
        dummyPaymentDto.setPaymentDate(dummyPayment.getPaymentDate().plusDays(1));
        dummyPaymentDto.setComment(dummyPayment.getComment() + UPDATE_SUFFIX);
        dummyPaymentDto.setAmount(dummyPayment.getAmount() + 1);
        dummyPaymentDto.setPaymentChannel(paymentChannelDto);
        dummyPaymentDto.setSubscriberId(SUBSCRIBER_ID);

        when(paymentChannelRepository.findById(any(Long.class))).thenReturn(Optional.of(DummyDataHelper.getDummyPaymentChannel()));
        when(subscriberRepository.findById(any(Long.class))).thenReturn(Optional.of(DummyDataHelper.getDummySubscriber()));
        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(dummyPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(dummyPayment);

        PaymentDto update = paymentService.update(dummyPaymentDto);

        assertThat(update).isNotNull();
        assertThat(update.getPeriod()).isEqualTo(dummyPaymentDto.getPeriod());
        assertThat(update.getPaymentDate()).isEqualTo(dummyPaymentDto.getPaymentDate());
        assertThat(update.getComment()).isEqualTo(dummyPaymentDto.getComment());
        assertThat(update.getAmount()).isEqualTo(dummyPaymentDto.getAmount());

        verify(paymentChannelRepository, times(1)).findById(any(Long.class));
        verify(subscriberRepository, times(1)).findById(any(Long.class));
        verify(paymentRepository, times(1)).findById(PAYMENT_ID);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("должен выбросить исключение при попытке обновить несуществующий платеж")
    public void shouldThrowExceptionWhileUpdateNotExistPayment() {
        PaymentDto dummyPaymentDto = DummyDataHelper.getDummyPaymentDto();
        dummyPaymentDto.setId(PAYMENT_ID);

        when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.empty());
        when(paymentRepository.save(any())).thenReturn(null);

        assertThatThrownBy(() -> paymentService.update(dummyPaymentDto))
                .isInstanceOf(EntityNotFoundException.class);

        verify(paymentRepository, times(1)).findById(PAYMENT_ID);
        verify(paymentRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("должен удалить существующий платеж")
    public void shouldDeleteExistPaymentById() {
        doNothing().when(paymentRepository).deleteById(PAYMENT_ID);

        paymentService.deleteById(PAYMENT_ID);

        verify(paymentRepository, times(1)).deleteById(PAYMENT_ID);
    }

}
