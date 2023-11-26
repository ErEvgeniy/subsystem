package ru.ermolaev.services.subscriber.manager.integration.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.ermolaev.services.report.models.model.request.ReportGenerationRequest;

@FeignClient(name = "report-generator")
public interface ReportGeneratorServiceProxy {

    @PostMapping(value = "/rg/v1/report")
    byte[] generateReport(ReportGenerationRequest reportGenerationRequest);

}
