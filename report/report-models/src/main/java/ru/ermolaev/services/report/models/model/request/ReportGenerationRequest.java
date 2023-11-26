package ru.ermolaev.services.report.models.model.request;

import lombok.Getter;
import lombok.Setter;
import ru.ermolaev.services.report.models.constant.ReportFileFormat;
import ru.ermolaev.services.report.models.constant.ReportTemplate;

@Getter
@Setter
public class ReportGenerationRequest {

    private byte[] data;

    private ReportTemplate template;

    private ReportFileFormat fileFormat;

}
