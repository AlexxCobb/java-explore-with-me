package ru.practicum.controller.privates;

import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.EventService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users/{userId}/comments")
@Validated
public class PrivateCommentController {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long userId,
                                    @RequestParam Long eventId,
                                    @RequestParam @Size(min = 10, max = 5000) String text) {
        log.info("Поступил POST-запрос на добавление комментария на event с id = {}, от user с id = {}", eventId, userId);
        return eventService.addComment(userId, eventId, text);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable Long userId,
                                    @PathVariable Long commentId,
                                    @RequestParam @Size(min = 10, max = 5000) String text) {
        log.info("Поступил PATCH-запрос на редактирование своего комментария c id = {}, от user с id = {}", commentId, userId);
        return eventService.updateComment(userId, commentId, text);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.info("Поступил DELETE-запрос на удаление своего комментария c id = {}, от user с id = {}", commentId, userId);
        eventService.deleteComment(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getAllUserComments(@PathVariable Long userId) {
        log.info("Поступил GET-запрос на получение всех комментариев, созданных пользователем user с id = {}", userId);
        return eventService.getAllUserComments(userId);
    }

    @GetMapping("/{eventId}")
    public List<CommentDto> getAllCommentsByUserEventId(@PathVariable Long userId,
                                                        @PathVariable Long eventId) {
        log.info("Поступил GET-запрос на получение всех комментариев пользователей, на событие с id = {}, созданного user с id = {}", eventId, userId);
        return eventService.getAllCommentsByUserEventId(userId, eventId);
    }

    @PostMapping("/{commentId}/like")
    public CommentDto addLike(@PathVariable Long userId,
                              @PathVariable Long commentId) {
        log.info("Поступил POST-запрос на добавление like на comment с id = {}, от user с id = {}", commentId, userId);
        return eventService.addLike(userId, commentId);
    }

    @PostMapping("/{commentId}/dislike")
    public CommentDto addDislike(@PathVariable Long userId,
                                 @PathVariable Long commentId) {
        log.info("Поступил POST-запрос на добавление dislike на comment с id = {}, от user с id = {}", commentId, userId);
        return eventService.addDislike(userId, commentId);
    }
}