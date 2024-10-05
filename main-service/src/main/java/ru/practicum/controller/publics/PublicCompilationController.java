package ru.practicum.controller.publics;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDtoResponse;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/compilations")
public class PublicCompilationController {

    private final EventService service;

    @GetMapping
    public List<CompilationDtoResponse> getAllCompilationsByParam(@RequestParam(defaultValue = "false") Boolean pinned,
                                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                                  @RequestParam(defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("Поступил GET-запрос на получение всех compilations с параметрами: pinned - {}, from - {}, size - {}", pinned, from, size);
        return service.findCompilationsByParam(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDtoResponse getCompilationById(@PathVariable Long compId) {
        log.info("Поступил GET-запрос на получение compilation с id - {}", compId);
        return service.findCompilationById(compId);
    }
}
