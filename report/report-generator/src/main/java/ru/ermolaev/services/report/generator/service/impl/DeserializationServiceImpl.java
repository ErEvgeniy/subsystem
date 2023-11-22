package ru.ermolaev.services.report.generator.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.ermolaev.services.report.generator.exception.ReportGenerationException;
import ru.ermolaev.services.report.generator.service.DeserializationService;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

@Slf4j
@Component
public class DeserializationServiceImpl implements DeserializationService {

    @Override
    public Object deserialize(byte[] binaryData) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(binaryData);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return objectInputStream.readObject();
        } catch (Exception ex) {
            log.error("Error occurred while deserialize report data", ex);
            throw new ReportGenerationException("Deserialize", "Error occurred while deserialize report data");
        }
    }

}
