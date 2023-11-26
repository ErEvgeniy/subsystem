package ru.ermolaev.services.subscriber.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.report.models.constant.ReportTemplate;
import ru.ermolaev.services.report.models.model.report.PaymentInfo;
import ru.ermolaev.services.report.models.model.report.SubscriberInfo;
import ru.ermolaev.services.report.models.model.report.container.SubscriberPaymentListReportData;
import ru.ermolaev.services.subscriber.manager.domain.Payment;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.repository.PaymentRepository;
import ru.ermolaev.services.subscriber.manager.repository.SubscriberRepository;
import ru.ermolaev.services.subscriber.manager.service.ReportDataCollectService;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriberPaymentListReportDataCollectService implements ReportDataCollectService {

    private final SubscriberRepository subscriberRepository;

    private final PaymentRepository paymentRepository;

    @Override
    public Object prepare(long subscriberId) {
        Subscriber subscriber = subscriberRepository.findById(subscriberId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Subscriber with id - %d not found", subscriberId)));

        List<PaymentInfo> paymentInfoList = paymentRepository.findAllBySubscriberId(subscriberId)
                .stream()
                .sorted(Comparator.comparing(Payment::getPaymentDate))
                .map(this::mapPayment)
                .toList();

        SubscriberInfo subscriberInfo = mapSubscriber(subscriber);

        SubscriberPaymentListReportData data = new SubscriberPaymentListReportData();
        data.setSubscriberInfo(subscriberInfo);
        data.setPaymentInfoList(paymentInfoList);
        return data;
    }

    @Override
    public boolean support(ReportTemplate template) {
        return ReportTemplate.SUBSCRIBER_PAYMENT_LIST.equals(template);
    }

    private PaymentInfo mapPayment(Payment payment) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setPaymentDate(payment.getPaymentDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        paymentInfo.setPaymentAmount(payment.getAmount().toString());
        return paymentInfo;
    }

    private SubscriberInfo mapSubscriber(Subscriber subscriber) {
        SubscriberInfo info = new SubscriberInfo();
        info.setFirstname(subscriber.getFirstname());
        info.setPatronymic(subscriber.getPatronymic());
        info.setLastname(subscriber.getLastname());
        info.setAccountNumber(subscriber.getAccountNumber());
        info.setCity(subscriber.getCity().getName());
        info.setStreet(subscriber.getStreet().getName());
        info.setHouse(subscriber.getHouse());
        info.setFlat(subscriber.getFlat());
        info.setConnectionDate(subscriber.getConnectionDate().toString());
        info.setBalance(subscriber.getBalance());
        return info;
    }

}
