package ru.practicum.controller.privates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.request.RequestDto;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/requests")
public class PrivateRequestController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto createRequest(@PathVariable Long userId,
                                    @RequestParam Long eventId) {
        log.info("Поступил POST-запрос на добавление запроса на event с id = {}, от user с id = {}", eventId, userId);
        return eventService.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        log.info("Поступил PATCH-запрос на отмену своего запроса c id = {}, на участие в событии от user с id = {}", requestId, userId);
        return eventService.cancelRequest(userId, requestId);
    }

    @GetMapping
    public List<RequestDto> getAllUserRequests(@PathVariable Long userId) {
        log.info("Поступил GET-запрос на получение информации о заявках на участие в чужих событиях от user с id = {}", userId);
        return eventService.getAllUserRequests(userId);
    }
}