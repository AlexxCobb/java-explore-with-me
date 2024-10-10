package ru.practicum.controller.privates;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.event.EventFullDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.UpdateEventDto;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.dto.request.RequestStatusUpdateDto;
import ru.practicum.dto.request.RequestStatusUpdateResult;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/events")
@Validated
public class PrivateEventController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(@PathVariable Long userId,
                                    @Valid @RequestBody NewEventDto eventDto) {
        log.info("Поступил POST-запрос на добавление event от user с id = {}", userId);
        return eventService.addEvent(userId, eventDto);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId,
                                    @PathVariable Long eventId,
                                    @RequestBody UpdateEventDto eventDto) {
        log.info("Поступил PATCH-запрос на обновление event с id = {}, user id = {}", eventId, userId);
        return eventService.updateEvent(userId, eventId, eventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        log.info("Поступил GET-запрос на получение event с id = {}, user id = {}", eventId, userId);
        return eventService.findUserEventById(userId, eventId);
    }

    @GetMapping()
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                             @RequestParam(defaultValue = "10") @PositiveOrZero Integer size) {
        log.info("Поступил GET-запрос на получение всех events, user с id = {}", userId);
        return eventService.findUserEvents(userId, from, size);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getUserEventRequestsById(@PathVariable Long userId,
                                                     @PathVariable Long eventId) {
        log.info("Поступил GET-запрос на получение информации о запросах на участие в event с id = {}, user id = {}", eventId, userId);
        return eventService.findEventRequestsById(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public RequestStatusUpdateResult manageRequest(@PathVariable Long userId,
                                                   @PathVariable Long eventId,
                                                   @RequestBody RequestStatusUpdateDto requestStatusUpdateDto) {
        log.info("Поступил PATCH-запрос на изменение статуса заявок event с id = {}, user id = {}", eventId, userId);
        return eventService.requestManager(userId, eventId, requestStatusUpdateDto);
    }
}