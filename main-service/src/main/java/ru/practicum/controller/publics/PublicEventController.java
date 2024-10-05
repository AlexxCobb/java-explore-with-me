package ru.practicum.controller.publics;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventParamPublic;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/events")
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventShortDto> getEvents(@Valid EventParamPublic eventParamPublic) {
        log.info("Поступил GET-запрос на получение events c параметрами: {}", eventParamPublic);
        return eventService.getEvents(eventParamPublic);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findEventById(@PathVariable Long eventId) {
        log.info("Поступил GET-запрос на получение подробной информации event c id: {}", eventId);
        return eventService.findEventById(eventId);
    }
}
