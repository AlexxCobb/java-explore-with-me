package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Request;

import java.util.List;

@Transactional(readOnly = true)
public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<Request> findByRequesterId(Long requesterId);

    List<Request> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<Request> findByEventId(Long eventId);
}
