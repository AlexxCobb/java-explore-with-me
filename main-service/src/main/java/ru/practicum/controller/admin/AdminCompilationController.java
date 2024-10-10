package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDtoResponse;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.service.EventService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final EventService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDtoResponse addCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Поступил POST-запрос на добавление compilation");
        return service.addCompilation(compilationDto);
    }

    @PatchMapping("/{compId}")
    public CompilationDtoResponse updateCompilation(@PathVariable Long compId, @RequestBody NewCompilationDto compilationDto) {
        log.info("Поступил PATCH-запрос на обновление compilation с id = {}", compId);
        return service.updateCompilation(compId, compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Поступил DELETE-запрос на удаление compilation с id = {}", compId);
        service.deleteCompilation(compId);
    }
}