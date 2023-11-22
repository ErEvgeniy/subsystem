package ru.ermolaev.services.report.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class ReportGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportGeneratorApplication.class, args);
    }

}
