package ru.ermolaev.services.subscriber.manager.util;

import org.jetbrains.annotations.NotNull;
import ru.ermolaev.services.subscriber.manager.domain.Charge;
import ru.ermolaev.services.subscriber.manager.domain.ChargeTarget;
import ru.ermolaev.services.subscriber.manager.domain.City;
import ru.ermolaev.services.subscriber.manager.domain.Notification;
import ru.ermolaev.services.subscriber.manager.domain.Payment;
import ru.ermolaev.services.subscriber.manager.domain.PaymentChannel;
import ru.ermolaev.services.subscriber.manager.domain.Street;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeDto;
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeTargetDto;
import ru.ermolaev.services.subscriber.manager.rest.dto.CityDto;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentChannelDto;
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentDto;
import ru.ermolaev.services.subscriber.manager.rest.dto.StreetDto;
import ru.ermolaev.services.subscriber.manager.rest.dto.SubscriberDto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DummyDataHelper {

    @NotNull
    public static City getDummyCity() {
        City city = new City();
        city.setExternalId(999L);
        city.setName("Test City");
        return city;
    }

    @NotNull
    public static CityDto getDummyCityDto() {
        CityDto cityDto = new CityDto();
        cityDto.setName("Test City");
        return cityDto;
    }

    @NotNull
    public static CityDto getDummyCityDto(Long id) {
        CityDto cityDto = getDummyCityDto();
        cityDto.setId(id);
        return cityDto;
    }

    @NotNull
    public static Street getDummyStreet() {
        Street street = new Street();
        street.setExternalId(999L);
        street.setName("Test Street");
        return street;
    }

    @NotNull
    public static StreetDto getDummyStreetDto() {
        StreetDto streetDto = new StreetDto();
        streetDto.setName("Test Street");
        return streetDto;
    }

    @NotNull
    public static StreetDto getDummyStreetDto(Long id) {
        StreetDto streetDto = getDummyStreetDto();
        streetDto.setId(id);
        return streetDto;
    }

    @NotNull
    public static ChargeTarget getDummyChargeTarget() {
        ChargeTarget chargeTarget = new ChargeTarget();
        chargeTarget.setExternalId(999L);
        chargeTarget.setName("Test ChargeTarget");
        return chargeTarget;
    }

    @NotNull
    public static ChargeTargetDto getDummyChargeTargetDto() {
        ChargeTargetDto chargeTargetDto = new ChargeTargetDto();
        chargeTargetDto.setName("Test ChargeTarget");
        return chargeTargetDto;
    }

    @NotNull
    public static ChargeTargetDto getDummyChargeTargetDto(Long id) {
        ChargeTargetDto chargeTargetDto = getDummyChargeTargetDto();
        chargeTargetDto.setId(id);
        return chargeTargetDto;
    }

    @NotNull
    public static PaymentChannel getDummyPaymentChannel() {
        PaymentChannel paymentChannel = new PaymentChannel();
        paymentChannel.setExternalId(999L);
        paymentChannel.setName("Test PaymentChannel");
        return paymentChannel;
    }

    @NotNull
    public static PaymentChannelDto getDummyPaymentChannelDto() {
        PaymentChannelDto paymentChannelDto = new PaymentChannelDto();
        paymentChannelDto.setName("Test PaymentChannel");
        return paymentChannelDto;
    }

    @NotNull
    public static PaymentChannelDto getDummyPaymentChannelDto(Long id) {
        PaymentChannelDto paymentChannelDto = getDummyPaymentChannelDto();
        paymentChannelDto.setId(id);
        return paymentChannelDto;
    }

    @NotNull
    public static Charge getDummyCharge() {
        Charge charge = new Charge();
        charge.setExternalId(999L);
        charge.setChargeDate(LocalDate.now());
        charge.setAmount(100.0F);
        charge.setPeriod("Test Period");
        charge.setComment("Test Comment");
        return charge;
    }

    @NotNull
    public static ChargeDto getDummyChargeDto() {
        ChargeDto chargeDto = new ChargeDto();
        chargeDto.setChargeDate(LocalDate.now());
        chargeDto.setAmount(100.0F);
        chargeDto.setPeriod("Test Period");
        chargeDto.setComment("Test Comment");
        return chargeDto;
    }

    @NotNull
    public static ChargeDto getDummyChargeDto(Long id) {
        ChargeDto chargeDto = getDummyChargeDto();
        chargeDto.setId(id);
        return chargeDto;
    }

    @NotNull
    public static Payment getDummyPayment() {
        Payment payment = new Payment();
        payment.setExternalId(999L);
        payment.setPaymentDate(LocalDate.now());
        payment.setAmount(100.0F);
        payment.setPeriod("Test Period");
        payment.setComment("Test Comment");
        return payment;
    }

    @NotNull
    public static PaymentDto getDummyPaymentDto() {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setPaymentDate(LocalDate.now());
        paymentDto.setAmount(100.0F);
        paymentDto.setPeriod("Test Period");
        paymentDto.setComment("Test Comment");
        return paymentDto;
    }

    @NotNull
    public static PaymentDto getDummyPaymentDto(Long id) {
        PaymentDto paymentDto = getDummyPaymentDto();
        paymentDto.setId(id);
        return paymentDto;
    }

    @NotNull
    public static Subscriber getDummySubscriber() {
        Subscriber subscriber = new Subscriber();
        subscriber.setExternalId(999L);
        subscriber.setFirstname("Test firstname");
        subscriber.setPatronymic("Test patronymic");
        subscriber.setLastname("Test lastname");
        subscriber.setContractNumber("00001");
        subscriber.setAccountNumber("00001");
        subscriber.setHouse(1);
        subscriber.setFlat(1);
        subscriber.setPhoneNumber("+70009995500");
        subscriber.setEmail("t@t.tu");
        subscriber.setIsActive(true);
        subscriber.setConnectionDate(LocalDate.now());
        return subscriber;
    }

    @NotNull
    public static SubscriberDto getDummySubscriberDto() {
        SubscriberDto subscriberDto = new SubscriberDto();
        subscriberDto.setFirstname("Test firstname");
        subscriberDto.setPatronymic("Test patronymic");
        subscriberDto.setLastname("Test lastname");
        subscriberDto.setContractNumber("00001");
        subscriberDto.setAccountNumber("00001");
        subscriberDto.setHouse(1);
        subscriberDto.setFlat(1);
        subscriberDto.setPhoneNumber("+70009995500");
        subscriberDto.setEmail("t@t.tu");
        subscriberDto.setIsActive(true);
        subscriberDto.setConnectionDate(LocalDate.now());
        return subscriberDto;
    }

    @NotNull
    public static SubscriberDto getDummySubscriberDto(Long id) {
        SubscriberDto subscriberDto = getDummySubscriberDto();
        subscriberDto.setId(id);
        return subscriberDto;
    }

    @NotNull
    public static Notification getDummyNotification() {
        Notification notification = new Notification();
        notification.setSentDate(LocalDateTime.now());
        notification.setStatus("Test Status");
        notification.setMessage("Test Message");
        notification.setChannel("Test Channel");
        notification.setDestination("Test Destination");
        notification.setReason("Test Reason");
        return notification;
    }

}
