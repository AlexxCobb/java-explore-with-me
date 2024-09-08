package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.DTO.StatRequestDto;
import ru.practicum.DTO.StatResponseDto;
import ru.practicum.service.StatisticService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatisticController {

    private final StatisticService service;

    @PostMapping("/hit")
    public void createHit(@Valid @RequestBody StatRequestDto statRequestDto) {
        log.info("Поступил POST-запрос на добавление статистики");
        service.createHit(statRequestDto);
    }

    @GetMapping("/stats")
    public List<StatResponseDto> getStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String start,
                                               @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") String end,
                                               @RequestParam(required = false) List<String> uris,
                                               @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Поступил GET-запрос на получение статистики с параметрами," +
                " начало - {}, конец - {}, события - {}, уникальность - {}", start, end, uris, unique);

        String decodeStart = URLDecoder.decode(start, StandardCharsets.UTF_8);
        String decodeEnd = URLDecoder.decode(end, StandardCharsets.UTF_8);
        return service.getStatHits(decodeStart, decodeEnd, uris, unique);
    }
}
