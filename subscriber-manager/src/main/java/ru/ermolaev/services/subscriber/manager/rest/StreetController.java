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
import ru.ermolaev.services.subscriber.manager.rest.dto.StreetDto;
import ru.ermolaev.services.subscriber.manager.service.StreetService;

import java.util.List;

@RestController
@RequestMapping("/sub/v1")
@RequiredArgsConstructor
public class StreetController {

    private final StreetService streetService;

    @GetMapping("/street")
    public List<StreetDto> list() {
        return streetService.findAll();
    }

    @GetMapping("/street/{id}")
    public StreetDto info(@PathVariable long id) {
        return streetService.findOneById(id);
    }

    @PostMapping("/street")
    public void create(@Valid @RequestBody StreetDto streetDto) {
        streetService.create(streetDto);
    }

    @PatchMapping("/street/{id}")
    public void update(@PathVariable long id, @Valid @RequestBody StreetDto streetDto) {
        streetDto.setId(id);
        streetService.update(streetDto);
    }

    @DeleteMapping("/street/{id}")
    public void delete(@PathVariable long id) {
        streetService.deleteById(id);
    }

}
