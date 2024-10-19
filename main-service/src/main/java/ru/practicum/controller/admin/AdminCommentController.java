package ru.practicum.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.EventService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final EventService eventService;

    @PatchMapping("/{commentId}")
    public CommentDto commentManager(@PathVariable Long commentId) {
        log.info("Поступил PATCH-запрос на модерацию комментария c id = {}", commentId);
        return eventService.manageComment(commentId);
    }
}
