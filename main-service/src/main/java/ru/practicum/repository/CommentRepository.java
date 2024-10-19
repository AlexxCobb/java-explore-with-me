package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.enums.State;
import ru.practicum.model.Comment;

import java.util.List;

@Transactional(readOnly = true)
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthorId(Long authorId);

    List<Comment> findAllByEventInitiatorIdAndEventIdAndStatus(Long authorId, Long eventId, State status);

    @Query("select c from Comment c where c.event.id = :eventId and c.status = PUBLISHED")
    List<Comment> findByEventId(Long eventId);

    boolean existsByEventIdAndAuthorId(Long eventId, Long authorId);
}