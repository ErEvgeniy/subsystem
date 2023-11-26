package ru.ermolaev.services.subscriber.manager.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.ermolaev.services.subscriber.manager.exception.BusinessException;
import ru.ermolaev.services.subscriber.manager.service.SerializationService;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

@Slf4j
@Service
public class SerializationServiceImpl implements SerializationService {

    @Override
    public byte[] serialize(Object object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception ex) {
            log.error("Error occurred while serialize report data", ex);
            throw new BusinessException("Report data serialization failed");
        }
    }

}
