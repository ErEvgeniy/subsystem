package ru.ermolaev.services.subscriber.manager.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ermolaev.services.subscriber.manager.rest.dto.SubscriberDto;
import ru.ermolaev.services.subscriber.manager.service.SubscriberService;

import java.util.List;

@RestController
@RequestMapping("/sub/v1")
@RequiredArgsConstructor
public class SubscriberController {

    private final SubscriberService subscriberService;

    @GetMapping("/subscriber")
    public List<SubscriberDto> list() {
        return subscriberService.findAll();
    }

    @GetMapping("/subscriber/{id}")
    public SubscriberDto info(@PathVariable long id) {
        return subscriberService.findOneById(id);
    }

    @PostMapping("/subscriber")
    public void create(@Valid @RequestBody SubscriberDto subscriberDto) {
        subscriberService.create(subscriberDto);
    }

    @PatchMapping("/subscriber/{id}")
    public void update(@PathVariable long id, @Valid @RequestBody SubscriberDto subscriberDto) {
        subscriberDto.setId(id);
        subscriberService.update(subscriberDto);
    }

    @DeleteMapping("/subscriber/{id}")
    public void delete(@PathVariable long id) {
        subscriberService.deleteById(id);
    }

}
