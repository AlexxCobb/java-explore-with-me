package ru.practicum.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventParamAdmin;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> getAllEventsWithParam(@Valid EventParamAdmin eventParamAdmin) {
        log.info("Поступил GET-запрос на получение events c параметрами: {}", eventParamAdmin);
        return eventService.getAllEventsWithParam(eventParamAdmin);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto eventManager(@PathVariable Long eventId, @RequestBody UpdateEventDto updateEventDto) {
        log.info("Поступил PATCH-запрос на редактирование данных event c id = {}", eventId);
        return eventService.eventManager(eventId, updateEventDto);
    }
}