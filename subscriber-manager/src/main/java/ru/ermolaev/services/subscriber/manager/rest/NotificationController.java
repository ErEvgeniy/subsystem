package ru.ermolaev.services.subscriber.manager.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.ermolaev.services.subscriber.manager.rest.dto.NotificationDto;
import ru.ermolaev.services.subscriber.manager.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/sub/v1")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/notification/subscriber/{id}")
    public void notify(
            @PathVariable long id,
            @RequestParam("template") String template,
            @RequestParam("channel") String channel
    ) {
        notificationService.notifySubscriber(id, template, channel);
    }

    @GetMapping("/notification/subscriber/{id}")
    public List<NotificationDto> list(@PathVariable long id) {
        return notificationService.findAllBySubscriberId(id);
    }

}
