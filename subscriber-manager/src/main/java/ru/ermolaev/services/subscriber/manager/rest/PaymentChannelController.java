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
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentChannelDto;
import ru.ermolaev.services.subscriber.manager.service.PaymentChannelService;

import java.util.List;

@RestController
@RequestMapping("/sub/v1")
@RequiredArgsConstructor
public class PaymentChannelController {

    private final PaymentChannelService paymentChannelService;

    @GetMapping("/payment-channel")
    public List<PaymentChannelDto> list() {
        return paymentChannelService.findAll();
    }

    @GetMapping("/payment-channel/{id}")
    public PaymentChannelDto info(@PathVariable long id) {
        return paymentChannelService.findOneById(id);
    }

    @PostMapping("/payment-channel")
    public void create(@Valid @RequestBody PaymentChannelDto paymentChannelDto) {
        paymentChannelService.create(paymentChannelDto);
    }

    @PatchMapping("/payment-channel/{id}")
    public void update(@PathVariable long id, @Valid @RequestBody PaymentChannelDto paymentChannelDto) {
        paymentChannelDto.setId(id);
        paymentChannelService.update(paymentChannelDto);
    }

    @DeleteMapping("/payment-channel/{id}")
    public void delete(@PathVariable long id) {
        paymentChannelService.deleteById(id);
    }

}
