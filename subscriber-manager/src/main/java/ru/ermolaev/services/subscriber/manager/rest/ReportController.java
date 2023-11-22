package ru.ermolaev.services.subscriber.manager.rest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.ermolaev.services.subscriber.manager.service.ReportService;

@RestController
@RequestMapping("/sub/v1")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/report/subscriber/{id}")
    public ResponseEntity<byte[]> generate(@PathVariable long id,
                                           @RequestParam("template") String template) {
        byte[] report = reportService.generateReport(id, template);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "report.pdf");

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }

}
