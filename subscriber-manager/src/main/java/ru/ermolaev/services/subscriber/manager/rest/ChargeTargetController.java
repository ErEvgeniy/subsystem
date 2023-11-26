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
import ru.ermolaev.services.subscriber.manager.rest.dto.ChargeTargetDto;
import ru.ermolaev.services.subscriber.manager.service.ChargeTargetService;

import java.util.List;

@RestController
@RequestMapping("/sub/v1")
@RequiredArgsConstructor
public class ChargeTargetController {

    private final ChargeTargetService chargeTargetService;

    @GetMapping("/charge-target")
    public List<ChargeTargetDto> list() {
        return chargeTargetService.findAll();
    }

    @GetMapping("/charge-target/{id}")
    public ChargeTargetDto info(@PathVariable long id) {
        return chargeTargetService.findOneById(id);
    }

    @PostMapping("/charge-target")
    public void create(@Valid @RequestBody ChargeTargetDto chargeTargetDto) {
        chargeTargetService.create(chargeTargetDto);
    }

    @PatchMapping("/charge-target/{id}")
    public void update(@PathVariable long id, @Valid @RequestBody ChargeTargetDto chargeTargetDto) {
        chargeTargetDto.setId(id);
        chargeTargetService.update(chargeTargetDto);
    }

    @DeleteMapping("/charge-target/{id}")
    public void delete(@PathVariable long id) {
        chargeTargetService.deleteById(id);
    }

}
