package ru.ermolaev.services.subscriber.manager.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.report.models.constant.ReportTemplate;
import ru.ermolaev.services.report.models.model.report.ChargeInfo;
import ru.ermolaev.services.report.models.model.report.SubscriberInfo;
import ru.ermolaev.services.report.models.model.report.container.SubscriberChargeListReportData;
import ru.ermolaev.services.subscriber.manager.domain.Charge;
import ru.ermolaev.services.subscriber.manager.domain.Subscriber;
import ru.ermolaev.services.subscriber.manager.repository.ChargeRepository;
import ru.ermolaev.services.subscriber.manager.repository.SubscriberRepository;
import ru.ermolaev.services.subscriber.manager.service.ReportDataCollectService;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriberChargeListReportDataCollectService implements ReportDataCollectService {

    private final SubscriberRepository subscriberRepository;

    private final ChargeRepository chargeRepository;

    @Override
    public Object prepare(long subscriberId) {
        Subscriber subscriber = subscriberRepository.findById(subscriberId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Subscriber with id - %d not found", subscriberId)));

        List<ChargeInfo> chargeInfoList = chargeRepository.findAllBySubscriberId(subscriberId)
                .stream()
                .sorted(Comparator.comparing(Charge::getChargeDate))
                .map(this::mapCharge)
                .toList();

        SubscriberInfo subscriberInfo = mapSubscriber(subscriber);

        SubscriberChargeListReportData data = new SubscriberChargeListReportData();
        data.setSubscriberInfo(subscriberInfo);
        data.setChargeInfoList(chargeInfoList);
        return data;
    }

    @Override
    public boolean support(ReportTemplate template) {
        return ReportTemplate.SUBSCRIBER_CHARGE_LIST.equals(template);
    }

    private ChargeInfo mapCharge(Charge charge) {
        ChargeInfo chargeInfo = new ChargeInfo();
        chargeInfo.setChargeDate(charge.getChargeDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        chargeInfo.setChargeAmount(charge.getAmount().toString());
        return chargeInfo;
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
