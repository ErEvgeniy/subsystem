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
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeDto;
import ru.ermolaev.services.subscriber.manager.service.ChargeService;

import java.util.List;

@RestController
@RequestMapping("/sub/v1")
@RequiredArgsConstructor
public class ChargeController {

    private final ChargeService chargeService;

    @GetMapping("/charge/subscriber/{id}")
    public List<ChargeDto> list(@PathVariable long id) {
        return chargeService.findAllBySubscriberId(id);
    }

    @GetMapping("/charge/{id}")
    public ChargeDto info(@PathVariable long id) {
        return chargeService.findOneById(id);
    }

    @PostMapping("/charge")
    public void create(@Valid @RequestBody ChargeDto chargeDto) {
        chargeService.create(chargeDto);
    }

    @PatchMapping("/charge/{id}")
    public void update(@PathVariable long id, @Valid @RequestBody ChargeDto chargeDto) {
        chargeDto.setId(id);
        chargeService.update(chargeDto);
    }

    @DeleteMapping("/charge/{id}")
    public void delete(@PathVariable long id) {
        chargeService.deleteById(id);
    }

}
