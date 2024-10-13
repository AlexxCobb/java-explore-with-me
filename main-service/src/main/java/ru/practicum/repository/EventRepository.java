package ru.practicum.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Event;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    boolean existsByIdAndInitiatorId(Long eventId, Long initiatorId);

    List<Event> findByInitiatorIdOrderByCreatedOnDesc(Long initiatorId, Pageable pageable);
}