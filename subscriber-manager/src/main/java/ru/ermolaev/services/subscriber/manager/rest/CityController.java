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
import ru.ermolaev.services.subscriber.manager.rest.dto.CityDto;
import ru.ermolaev.services.subscriber.manager.service.CityService;

import java.util.List;

@RestController
@RequestMapping("/sub/v1")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping("/city")
    public List<CityDto> list() {
        return cityService.findAll();
    }

    @GetMapping("/city/{id}")
    public CityDto info(@PathVariable long id) {
        return cityService.findOneById(id);
    }

    @PostMapping("/city")
    public void create(@Valid @RequestBody CityDto cityDto) {
        cityService.create(cityDto);
    }

    @PatchMapping("/city/{id}")
    public void update(@PathVariable long id, @Valid @RequestBody CityDto cityDto) {
        cityDto.setId(id);
        cityService.update(cityDto);
    }

    @DeleteMapping("/city/{id}")
    public void delete(@PathVariable long id) {
        cityService.deleteById(id);
    }

}
