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
import ru.ermolaev.services.subscriber.manager.rest.dto.PaymentDto;
import ru.ermolaev.services.subscriber.manager.service.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/sub/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/payment/subscriber/{id}")
    public List<PaymentDto> list(@PathVariable long id) {
        return paymentService.findAllBySubscriberId(id);
    }

    @GetMapping("/payment/{id}")
    public PaymentDto info(@PathVariable long id) {
        return paymentService.findOneById(id);
    }

    @PostMapping("/payment")
    public void create(@Valid @RequestBody PaymentDto paymentDto) {
        paymentService.create(paymentDto);
    }

    @PatchMapping("/payment/{id}")
    public void update(@PathVariable long id, @Valid @RequestBody PaymentDto paymentDto) {
        paymentDto.setId(id);
        paymentService.update(paymentDto);
    }

    @DeleteMapping("/payment/{id}")
    public void delete(@PathVariable long id) {
        paymentService.deleteById(id);
    }

}
