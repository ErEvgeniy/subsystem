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
    public static Street getDummyStreet() {
        Street street = new Street();
        street.setExternalId(999L);
        street.setName("Test Street");
        return street;
    }

    @NotNull
    public static ChargeTarget getDummyChargeTarget() {
        ChargeTarget chargeTarget = new ChargeTarget();
        chargeTarget.setExternalId(999L);
        chargeTarget.setName("Test ChargeTarget");
        return chargeTarget;
    }

    @NotNull
    public static PaymentChannel getDummyPaymentChannel() {
        PaymentChannel paymentChannel = new PaymentChannel();
        paymentChannel.setExternalId(999L);
        paymentChannel.setName("Test PaymentChannel");
        return paymentChannel;
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
    public static Subscriber getDummySubscriber() {
        Subscriber subscriber = new Subscriber();
        subscriber.setExternalId(999L);
        subscriber.setFirstname("Test firstname");
        subscriber.setPatronymic("Test patronymic");
        subscriber.setLastname("Test lastname");
        subscriber.setContractNumber("Test contractNumber");
        subscriber.setAccountNumber("Test accountNumber");
        subscriber.setHouse(1);
        subscriber.setFlat(1);
        subscriber.setPhoneNumber("Test phoneNumber");
        subscriber.setEmail("Test email");
        subscriber.setIsActive(true);
        subscriber.setConnectionDate(LocalDate.now());
        return subscriber;
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
