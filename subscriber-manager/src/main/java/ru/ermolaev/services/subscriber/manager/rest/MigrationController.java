package ru.ermolaev.services.subscriber.manager.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.ermolaev.services.subscriber.manager.service.MigrationService;

@RestController
@RequestMapping("/sub/v1")
@RequiredArgsConstructor
public class MigrationController {

    private final MigrationService migrationService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/migration")
    public void init() {
        migrationService.sendRequestForMigration();
    }

}
